package com.example.imageview.utils;

import java.text.NumberFormat;
import java.util.Locale;
public class CurrencyUtil {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    static {
        CURRENCY_FORMAT.setMaximumFractionDigits(0);
    }

    public static String formatRupiah(double amount) {
        return CURRENCY_FORMAT.format(amount).replace("Rp", "Rp ");
    }
}
