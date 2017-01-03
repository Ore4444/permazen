
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package org.jsimpledb.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.jsimpledb.JSimpleDB;

/**
 * Verify the JSimpleDB schema auto-generated from user-supplied model classes.
 *
 * <p>
 * This goal verifies that the JSimpleDB schema can be successfully auto-generated from user-supplied model classes.
 * It also checks that the schema will not lead to any {@link org.jsimpledb.core.SchemaMismatchException}s at runtime.
 *
 * <p>
 * Such exceptions occur when either:
 * <ul>
 *  <li>There is already a schema recorded in the database under the configured schema version,
 *      but the current schema version is not compatible with it; or
 *  <li>The current schema version conflicts with one or more other schema versions
 *      recorded in the database, e.g., because a field has changed its type.
 * </ul>
 *
 * <p>
 * The first case is detected by having an expected schema XML file. This file corresponds to the project's
 * currently configured JSimpleDB schema version number (which is used to configure JSimpleDB at runtime).
 * It is used to verify that the schema auto-generated from the project's current model classes has not changed
 * in an incompatible way, which would cause an error at runtime.
 * In other words, the actual schema generated from the compiled classes is verified to match
 * what is expected, which is recorded in this file.
 * The current expected schema XML file location is configured by {@code <expectedSchemaFile>};
 *
 * <p>
 * The second case it detected by supplying XML files containing any old schema versions that are still active
 * in the old schema files directory. All files ending in {@code .xml} found anywhere under this
 * directory are checked for incompatibilities with the current schema version.
 * The old schema XML files directory is configured by {@code <oldSchemasDirectory>}.
 *
 * <p>
 * If this goal fails due to an incompatibility of the first type:
 * <ul>
 *  <li>The old expected schema XML file should be moved into the old schemas directory;</li>
 *  <li>the new expected schema XML file (found in the location configured by {@code <actualSchemaFile>})
 *      should be copied to the current expected schema XML file;</li>
 *  <li>The project's configured JSimpleDB schema version number should be incremented.</li>
 * </ul>
 *
 * <p>
 * If this goal fails due to an incompatibility of the second type, you must adjust your model classes to make
 * them compatible again. For example, manually reassign the conflicting field a different
 * {@link org.jsimpledb.annotation.JField#name name()} or {@link org.jsimpledb.annotation.JField#storageId storageId()}
 * using the {@link org.jsimpledb.annotation.JField} annotation.
 */
@Mojo(name = "verify",
  defaultPhase = LifecyclePhase.PROCESS_CLASSES,
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
  threadSafe = true)
public class VerifySchemaMojo extends AbstractMainSchemaMojo {

    public static final String OLD_SCHEMAS_DEFAULT = GenerateSchemaMojo.JSIMPLEDB_DIRECTORY_DEFAULT + "/old";

    /**
     * The schema XML file that contains the expected current schema.
     */
    @Parameter(defaultValue = GenerateSchemaMojo.EXPECTED_SCHEMA_DEFAULT, property = "expectedSchemaFile")
    private File expectedSchemaFile;

    /**
     * The schema XML file generated by this goal that contains the actual current schema.
     */
    @Parameter(defaultValue = "${project.build.directory}/actual-jsimpledb-schema.xml", property = "actualSchemaFile")
    private File actualSchemaFile;

    /**
     * Whether to automatically generate the expected schema file if it does not already exist.
     * If set to {@code false}, this goal will fail instead and leave the actual schema XML file
     * in the {@code <actualSchemaFile>} location.
     */
    @Parameter(defaultValue = "true")
    private boolean autoGenerate;

    /**
     * Whether to verify not only schema compatibility but also that the two schemas are identical, i.e.,
     * the same names are used for object types, fields, and composite indexes.
     *
     * <p>
     * Two schemas that are equivalent except for names are considered compatible, because the core API uses
     * storage ID's, not names, to encode fields. However, if names change then some JSimpleDB layer operations,
     * such as index queries and reference path inversion, may need to be updated.
     */
    @Parameter(defaultValue = "true")
    private boolean matchNames;

    /**
     * The directory containing old schema versions still in use in any existing databases.
     */
    @Parameter(defaultValue = VerifySchemaMojo.OLD_SCHEMAS_DEFAULT, property = "oldSchemasDirectory")
    private File oldSchemasDirectory;

    @Override
    protected void execute(JSimpleDB jdb) throws MojoExecutionException, MojoFailureException {

        // Handle the case where the expected schema file doesn't exist
        if (!this.expectedSchemaFile.exists()) {
            if (!this.autoGenerate)
                throw new MojoFailureException("expected schema file " + expectedSchemaFile + " does not exist");
            this.generate(jdb.getSchemaModel(), this.expectedSchemaFile);
            return;
        }

        // Verify actual vs. expected
        if (!this.verify(jdb.getSchemaModel(), this.expectedSchemaFile, this.matchNames)) {
            this.getLog().info("Recommended actions to take:\n"
              + "  (a) If no schema change was intended, undo whatever Java model class change(s) caused the schema difference.\n"
              + "  (b) Otherwise:\n"
              + "      1. Move " + this.expectedSchemaFile + " into " + this.oldSchemasDirectory + "\n"
              + "      2. Copy " + this.actualSchemaFile + " to " + this.expectedSchemaFile + "\n"
              + "      3. Update your configured JSimpleDB schema version number (or use -1 to auto-generate)");
        }

        // Gather old schema files
        final ArrayList<File> oldSchemaFiles = new ArrayList<>();
        if (this.oldSchemasDirectory.exists()) {
            try {
                Files.walkFileTree(this.oldSchemasDirectory.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        if (path.getNameCount() != 0 && path.getName(path.getNameCount() - 1).toString().endsWith(".xml"))
                            oldSchemaFiles.add(path.toFile());
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new MojoExecutionException("error walking output directory hierarchy", e);
            }
        }

        // Verify compatibility with old schema versions
        if (!this.verify(jdb, oldSchemaFiles.iterator()))
            throw new MojoFailureException("current schema conflicts with one or more old schema versions");
    }
}
