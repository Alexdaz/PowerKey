#ifndef POWERKEY_POWERKEY_H
#define POWERKEY_POWERKEY_H

#include <jni.h>

#define INDEX_MIN 0
#define PERCENTAGE_MIN 0
#define PERCENTAGE_MAX 99
#define NON_LETTER_INJECTION_PERCENT 25
#define CONSONANT_VOWEL_PATTERN_MOD 2

#define LIMIT_CHARS_EASYPASS 10
#define LIMIT_BY_TYPE_CHARS 3

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL
Java_net_alex_powerkey_MainActivity_generateRandomPassword(
        JNIEnv* env, jobject /* this */,
        jint length, jboolean useUpper, jboolean useLower,
        jboolean useDigits, jboolean useSpecial);

JNIEXPORT jstring JNICALL
Java_net_alex_powerkey_MainActivity_generateMemorablePassword(
        JNIEnv* env, jobject /* this */,
        jint length, jboolean useUpper, jboolean useLower,
        jboolean useDigits, jboolean useSpecial);

JNIEXPORT jstring JNICALL
Java_net_alex_powerkey_MainActivity_generateEasyPassword(
        JNIEnv* env, jobject /* this */);

#ifdef __cplusplus
}
#endif

#endif //POWERKEY_POWERKEY_H
