package FileStore.Code;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 编码工具类
 * Created by rx on 2017/9/2.
 */
public class CodeUtils {

    private static final Map<String, TypeKind> KINDS = initKinds();

    /**
     * 类型集合初始化
     * @return
     */
    private static Map<String, TypeKind> initKinds() {
        Map<String, TypeKind> kinds = new HashMap<String, TypeKind>();

        kinds.put("boolean", TypeKind.BOOLEAN);

        kinds.put("byte", TypeKind.BYTE);

        kinds.put("char", TypeKind.CHAR);

        kinds.put("short", TypeKind.SHORT);

        kinds.put("int", TypeKind.INT);

        kinds.put("long", TypeKind.LONG);

        kinds.put("float", TypeKind.FLOAT);

        kinds.put("double", TypeKind.DOUBLE);

        kinds.put("java.lang.String", TypeKind.STRING);

        kinds.put("java.lang.Boolean", TypeKind.BOOLEAN_OBJ);

        kinds.put("java.lang.Byte", TypeKind.BYTE_OBJ);

        kinds.put("java.lang.Character", TypeKind.CHAR_OBJ);

        kinds.put("java.lang.Short", TypeKind.SHORT_OBJ);

        kinds.put("java.lang.Integer", TypeKind.INT_OBJ);

        kinds.put("java.lang.Long", TypeKind.LONG_OBJ);

        kinds.put("java.lang.Float", TypeKind.FLOAT_OBJ);

        kinds.put("java.lang.Double", TypeKind.DOUBLE_OBJ);

        kinds.put("java.util.Date", TypeKind.DATE);

        return kinds;
    }

    /**
     * 除了 NULL的所有类型
     * @param type
     * @return
     */
    public static TypeKind kindOf(Class<?> type) {
        String typeName = type.getName();
        TypeKind kind = CodeUtils.KINDS.get(typeName);

        if (kind == null) {
            kind = TypeKind.OBJECT;
        }

        return kind;
    }

    /**
     * 所有类型包括NULL
     * @param value
     * @return
     */
    public static TypeKind kindOf(Object value) {
        if (value == null) {
            return TypeKind.NULL;
        }

        String typeName = value.getClass().getName();
        TypeKind kind = CodeUtils.KINDS.get(typeName);

        if (kind == null) {
            kind = TypeKind.OBJECT;
        }

        return kind;
    }
    public static void encode(Object value, ByteBuffer buf) {
        TypeKind kind = kindOf(value);

        int ordinal = kind.ordinal();
        assert ordinal < 128;

        byte kindCode = (byte) ordinal;
        buf.put(kindCode);
    }
}
