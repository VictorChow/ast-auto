package me.victor.lombok.core.constant;

/**
 * 访问级别常量
 * @author binbin.hou
 * @since 0.0.7
 */
public interface Flags {
    int PUBLIC = 1;
    int PRIVATE = 2;
    int PROTECTED = 4;
    int STATIC = 8;
    int FINAL = 16;
    int SYNCHRONIZED = 32;
    int VOLATILE = 64;
    int TRANSIENT = 128;
    int NATIVE = 256;
    int INTERFACE = 512;
    int ABSTRACT = 1024;
    int STRICTFP = 2048;
    int SYNTHETIC = 4096;
    int ANNOTATION = 8192;
    int ENUM = 16384;
    int MANDATED = 32768;
    int StandardFlags = 4095;
    int ACC_SUPER = 32;
    int ACC_BRIDGE = 64;
    int ACC_VARARGS = 128;
    int DEPRECATED = 131072;
    int HASINIT = 262144;
    int BLOCK = 1048576;
    int IPROXY = 2097152;
    int NOOUTERTHIS = 4194304;
    int EXISTS = 8388608;
    int COMPOUND = 16777216;
    int CLASS_SEEN = 33554432;
    int SOURCE_SEEN = 67108864;
    int LOCKED = 134217728;
    int UNATTRIBUTED = 268435456;
    int ANONCONSTR = 536870912;
    int ACYCLIC = 1073741824;
    long BRIDGE = 2147483648L;
    long PARAMETER = 8589934592L;
    long VARARGS = 17179869184L;
    long ACYCLIC_ANN = 34359738368L;
    long GENERATEDCONSTR = 68719476736L;
    long HYPOTHETICAL = 137438953472L;
    long PROPRIETARY = 274877906944L;
    long UNION = 549755813888L;
    long OVERRIDE_BRIDGE = 1099511627776L;
    long EFFECTIVELY_FINAL = 2199023255552L;
    long CLASH = 4398046511104L;
    long DEFAULT = 8796093022208L;
    long AUXILIARY = 17592186044416L;
    long NOT_IN_PROFILE = 35184372088832L;
    long BAD_OVERRIDE = 35184372088832L;
    long SIGNATURE_POLYMORPHIC = 70368744177664L;
    long THROWS = 140737488355328L;
    long POTENTIALLY_AMBIGUOUS = 281474976710656L;
    long LAMBDA_METHOD = 562949953421312L;
    long TYPE_TRANSLATED = 1125899906842624L;
    int AccessFlags = 7;
    int LocalClassFlags = 23568;
    int MemberClassFlags = 24087;
    int ClassFlags = 32273;
    int InterfaceVarFlags = 25;
    int VarFlags = 16607;
    int ConstructorFlags = 7;
    int InterfaceMethodFlags = 1025;
    int MethodFlags = 3391;
    long ExtendedStandardFlags = 8796093026303L;
    long ModifierFlags = 8796093025791L;
    long InterfaceMethodMask = 8796093025289L;
    long AnnotationTypeElementMask = 1025L;
    long LocalVarFlags = 8589934608L;
    long ReceiverParamFlags = 8589934592L;

}
