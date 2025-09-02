#ifndef POWERKEY_PASSWORDCHARS_H
#define POWERKEY_PASSWORDCHARS_H

#include <string_view>

namespace PasswordChars {
    //Generic Pools
    constexpr std::string_view Upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    constexpr std::string_view Lower = "abcdefghijklmnopqrstuvwxyz";
    constexpr std::string_view Digits = "0123456789";
    constexpr std::string_view Special = "!@#$%^&*()-_=+[]{}|;:,.<>?/";
    //Memorable
    constexpr std::string_view VowelsL = "aeiou";
    constexpr std::string_view ConsonL = "bcdfghjklmnpqrstvwxyz";
    constexpr std::string_view VowelsU = "AEIOU";
    constexpr std::string_view ConsonU = "BCDFGHJKLMNPQRSTVWXYZ";
}

#endif //POWERKEY_PASSWORDCHARS_H
