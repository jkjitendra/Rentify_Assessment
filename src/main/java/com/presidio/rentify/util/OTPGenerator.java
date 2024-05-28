package com.presidio.rentify.util;

import java.util.Random;

public class OTPGenerator {

    public static Integer generateOTP() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
