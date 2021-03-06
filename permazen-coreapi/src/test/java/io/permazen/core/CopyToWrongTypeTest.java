
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package io.permazen.core;

import io.permazen.core.util.ObjIdMap;
import io.permazen.kv.simple.SimpleKVDatabase;
import io.permazen.schema.SchemaModel;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import org.testng.annotations.Test;

public class CopyToWrongTypeTest extends CoreAPITestSupport {

    @Test
    public void testCopyToWrongType() throws Exception {

        // Setup database

        final SimpleKVDatabase kvstore = new SimpleKVDatabase();

        final SchemaModel schema = SchemaModel.fromXML(new ByteArrayInputStream((
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<Schema formatVersion=\"1\">\n"
          + "  <ObjectType name=\"Foo\" storageId=\"1\">\n"
          + "    <SimpleField name=\"long\" type=\"long\" storageId=\"2\"/>\n"
          + "  </ObjectType>\n"
          + "  <ObjectType name=\"Bar\" storageId=\"3\">\n"
          + "    <SimpleField name=\"uuid\" type=\"java.util.UUID\" storageId=\"4\"/>\n"
          + "    <ReferenceField name=\"ref\" storageId=\"5\" allowDeleted=\"true\">\n"
          + "      <ObjectTypes>\n"
          + "         <ObjectType storageId=\"3\"/>\n"
          + "      </ObjectTypes>\n"
          + "    </ReferenceField>\n"
          + "  </ObjectType>\n"
          + "</Schema>\n"
          ).getBytes("UTF-8")));

        final Database db = new Database(kvstore);

        Transaction tx = db.createTransaction(schema, 1, true);

        final ObjId foo1 = tx.create(1);
        final ObjId foo2 = tx.create(1);
        tx.writeSimpleField(foo1, 2, 1234L, false);
        final ObjId bar1 = tx.create(3);
        final ObjId bar2 = tx.create(3);
        tx.writeSimpleField(bar1, 4, UUID.randomUUID(), false);
        tx.writeSimpleField(bar2, 4, UUID.randomUUID(), false);

        // Try to copy mapping bar1 -> foo1: incompatible type
        final ObjIdMap<ObjId> remap = new ObjIdMap<>(1);
        remap.put(bar1, foo1);
        try {
            tx.copy(bar1, tx, false, false, null, remap);
            assert false : "copied foo1 to bar1!";
        } catch (IllegalArgumentException e) {
            this.log.debug("got expected " + e);
        }

        // Try to copy mapping bar1.ref -> foo1: incompatible type in reference field
        tx.writeSimpleField(bar1, 5, bar2, false);
        remap.clear();
        remap.put(bar2, foo2);
        try {
            tx.copy(bar1, tx, false, false, null, remap);
            assert false : "copied bar1.ref to foo!";
        } catch (IllegalArgumentException e) {
            this.log.debug("got expected " + e);
        }

        // Try to copy mapping bar2 -> wrong storage ID
        remap.clear();
        remap.put(bar2, new ObjId(1));
        try {
            tx.copy(bar2, tx, false, false, null, remap);
            assert false : "copied bar2 to id#1!";
        } catch (IllegalArgumentException e) {
            this.log.debug("got expected " + e);
        }

        // Try to copy mapping bar2 -> null
        remap.clear();
        remap.put(bar2, null);
        try {
            tx.copy(bar2, tx, false, false, null, remap);
            assert false : "copied bar2 to null!";
        } catch (IllegalArgumentException e) {
            this.log.debug("got expected " + e);
        }
    }
}

