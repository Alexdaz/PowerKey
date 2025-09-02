#include <random>
#include <string>
#include <jni.h>

#include "powerkey.h"
#include "PasswordChars.h"

static std::mt19937 rng{std::random_device{}()};

extern "C" JNIEXPORT jstring JNICALL
Java_net_alex_powerkey_MainActivity_generateRandomPassword(JNIEnv* env, jobject,
                                                           jint length, jboolean useU, jboolean useL,
                                                           jboolean useD, jboolean useS)
{
    std::string pool;
    if (useU) pool += PasswordChars::Upper;
    if (useL) pool += PasswordChars::Lower;
    if (useD) pool += PasswordChars::Digits;
    if (useS) pool += PasswordChars::Special;

    std::vector<char> result;
    result.reserve(length);

    auto pick = [&](std::string_view chars){
        std::uniform_int_distribution<size_t> dist(INDEX_MIN, chars.size()-1);
        result.push_back(chars[dist(rng)]);
    };

    if (useU) pick(PasswordChars::Upper);
    if (useL) pick(PasswordChars::Lower);
    if (useD) pick(PasswordChars::Digits);
    if (useS) pick(PasswordChars::Special);

    std::uniform_int_distribution<size_t> poolDist(INDEX_MIN, pool.size()-1);
    while (result.size() < static_cast<size_t>(length))
    {
        result.push_back(pool[poolDist(rng)]);
    }

    std::shuffle(result.begin(), result.end(), rng);
    std::string out(result.begin(), result.end());

    return env->NewStringUTF(out.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_net_alex_powerkey_MainActivity_generateMemorablePassword(JNIEnv* env, jobject,jint length,
                                                              jboolean useU, jboolean useL,
                                                              jboolean useD, jboolean useS)
{
    std::string vowels, cons, nonLetters;
    if (useL) { vowels += PasswordChars::VowelsL; cons += PasswordChars::ConsonL; }
    if (useU) { vowels += PasswordChars::VowelsU; cons += PasswordChars::ConsonU; }
    if (useD) nonLetters += PasswordChars::Digits;
    if (useS) nonLetters += PasswordChars::Special;

    if (vowels.empty() && cons.empty() && nonLetters.empty()) { return env->NewStringUTF(""); }

    std::string result;
    result.reserve(length);

    std::uniform_int_distribution<size_t> vDist(INDEX_MIN, std::max(INDEX_MIN, (int)vowels.size()-1));
    std::uniform_int_distribution<size_t> cDist(INDEX_MIN, std::max(INDEX_MIN, (int)cons.size()-1));
    std::uniform_int_distribution<size_t> nDist(INDEX_MIN, std::max(INDEX_MIN, (int)nonLetters.size()-1));
    std::uniform_int_distribution<int>       pctDist(PERCENTAGE_MIN, PERCENTAGE_MAX);

    bool haveLetters    = !vowels.empty() && !cons.empty();
    bool haveNonLetters = !nonLetters.empty();
    int  nonLetPct      = haveNonLetters ? NON_LETTER_INJECTION_PERCENT : 0;

    for (int i = 0; i < length; ++i)
    {
        if (!haveLetters && haveNonLetters)
        {
            result += nonLetters[nDist(rng)];
        }
        else if (haveNonLetters && pctDist(rng) < nonLetPct)
        {
            result += nonLetters[nDist(rng)];
        }
        else if (i % CONSONANT_VOWEL_PATTERN_MOD == 0)
        {
            result += cons[cDist(rng)];
        }
        else
        {
            result += vowels[vDist(rng)];
        }
    }

    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_net_alex_powerkey_MainActivity_generateEasyPassword(JNIEnv* env, jobject)
{
    std::uniform_int_distribution<size_t> uDist(INDEX_MIN, PasswordChars::Upper.size() - 1);
    std::uniform_int_distribution<size_t> dDist(INDEX_MIN, PasswordChars::Digits.size() - 1);
    std::uniform_int_distribution<size_t> lDist(INDEX_MIN, PasswordChars::Lower.size() - 1);
    std::uniform_int_distribution<size_t> sDist(INDEX_MIN, PasswordChars::Special.size() - 1);

    std::string result;
    result.reserve(LIMIT_CHARS_EASYPASS);

    for (int i = 0; i < LIMIT_BY_TYPE_CHARS; ++i) result += PasswordChars::Upper[uDist(rng)];
    for (int i = 0; i < LIMIT_BY_TYPE_CHARS; ++i) result += PasswordChars::Digits[dDist(rng)];
    for (int i = 0; i < LIMIT_BY_TYPE_CHARS; ++i) result += PasswordChars::Lower[lDist(rng)];
    result += PasswordChars::Special[sDist(rng)];

    return env->NewStringUTF(result.c_str());
}