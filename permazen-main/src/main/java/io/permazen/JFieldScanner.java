
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package io.permazen;

import io.permazen.annotation.JField;
import io.permazen.annotation.PermazenType;
import io.permazen.core.DeleteAction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Scans for {@link JField &#64;JField} annotations.
 */
class JFieldScanner<T> extends AbstractFieldScanner<T, JField> {

    JFieldScanner(JClass<T> jclass, PermazenType permazenType) {
        super(jclass, JField.class, permazenType);
    }

    @Override
    protected JField getDefaultAnnotation() {
        return JFieldScanner.getDefaultJField(this.permazenType);
    }

    @Override
    protected boolean includeMethod(Method method, JField annotation) {
        this.checkNotStatic(method);
        this.checkParameterTypes(method);
        if (method.getReturnType().equals(Void.TYPE))
            throw new IllegalArgumentException(this.getErrorPrefix(method) + "method returns void");
        return true;
    }

    @Override
    protected boolean isAutoPropertyCandidate(Method method) {
        if (!super.isAutoPropertyCandidate(method))
            return false;
        final Class<?> returnType = method.getReturnType();
        if (List.class.isAssignableFrom(returnType)
          || Set.class.isAssignableFrom(returnType)
          || Map.class.isAssignableFrom(returnType))
            return false;
        if (returnType != Counter.class) {
            final Method setter;
            try {
                setter = Util.findJFieldSetterMethod(this.jclass.type, method);
            } catch (IllegalArgumentException e) {
                return false;
            }
            if (!this.permazenType.autogenNonAbstract() && (setter.getModifiers() & Modifier.ABSTRACT) == 0)
                return false;
        }
        return true;
    }

    public static final JField getDefaultJField(final PermazenType permazenType) {
        return new DefaultJField(permazenType);
    }

// DefaultJField

    public static class DefaultJField implements JField {

        private PermazenType permazenType;

        DefaultJField(PermazenType permazenType) {
            this.permazenType = permazenType;
        }

        @Override
        public Class<JField> annotationType() {
            return JField.class;
        }
        @Override
        public String name() {
            return "";
        }
        @Override
        public String type() {
            return "";
        }
        @Override
        public long typeSignature() {
            return 0;
        }
        @Override
        public int storageId() {
            return 0;
        }
        @Override
        public boolean indexed() {
            return false;
        }
        @Override
        public boolean unique() {
            return false;
        }
        @Override
        public String[] uniqueExclude() {
            return new String[0];
        }
        @Override
        public String[] cascades() {
            return new String[0];
        }
        @Override
        public String[] inverseCascades() {
            return new String[0];
        }
        @Override
        public DeleteAction onDelete() {
            return DeleteAction.EXCEPTION;
        }
        @Override
        public boolean cascadeDelete() {
            return false;
        }
        @Override
        public boolean allowDeleted() {
            return this.permazenType.autogenAllowDeleted();
        }
        @Override
        public boolean allowDeletedSnapshot() {
            return this.permazenType.autogenAllowDeletedSnapshot();
        }
        @Override
        public UpgradeConversionPolicy upgradeConversion() {
            return this.permazenType.autogenUpgradeConversion();
        }
    };
}
