package com.flytekart.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.flytekart.models.Address;
import com.google.common.base.CaseFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utilities {
    public static SharedPreferences getSharedPreferences() {
        return com.flytekart.Flytekart.getInstance().getApplicationContext().getSharedPreferences(
                com.flytekart.Flytekart.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    public static String getAddressString(Address address) {
        if (address != null) {
            String addressString = address.getLine1() + ", " + address.getLine2() + ", " + address.getCity();
            return addressString;
        } else {
            return Constants.EMPTY;
        }
    }

    public static String getFormattedCalendarString(String timeString) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sourceSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.ENGLISH);
        SimpleDateFormat resultSdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        try {
            cal.setTime(sourceSdf.parse(timeString));
            return resultSdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }

    public static String getFormattedCalendarString(String timeString, String toFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sourceSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.ENGLISH);
        SimpleDateFormat resultSdf = new SimpleDateFormat(toFormat, Locale.ENGLISH);
        try {
            cal.setTime(sourceSdf.parse(timeString));
            return resultSdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }

    public static String getFormattedTodayCalendarString(String toFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat resultSdf = new SimpleDateFormat(toFormat, Locale.ENGLISH);
        return resultSdf.format(cal.getTime());
    }

    public static String getFormattedOrderStatus(String orderStatus) {
        switch (orderStatus) {
            case Constants.OrderStatus.IN_PROGRESS:
                return "In progress";
            case Constants.OrderStatus.PLACED:
                return "Placed";
            case Constants.OrderStatus.CANCELED:
                return "Canceled";
            case Constants.OrderStatus.ACCEPTED:
                return "Accepted";
            case Constants.OrderStatus.PROCESSING:
                return "Processing";
            case Constants.OrderStatus.PROCESSED:
                return "Processed";
            case Constants.OrderStatus.OUT_FOR_DELIVERY:
                return "Out for delivery";
            case Constants.OrderStatus.DELIVERED:
                return "Delivered";
            default:
                return Constants.EMPTY;
        }
    }

    public static String getNextFormattedOrderStatus(String orderStatus) {
        switch (orderStatus) {
            case Constants.OrderStatus.IN_PROGRESS:
                return "Placed";
            case Constants.OrderStatus.PLACED:
                return "Accepted";
            case Constants.OrderStatus.ACCEPTED:
                return "Processing";
            case Constants.OrderStatus.PROCESSING:
                return "Processed";
            case Constants.OrderStatus.PROCESSED:
                return "Out for delivery";
            case Constants.OrderStatus.OUT_FOR_DELIVERY:
                return "Delivered";
            default:
                return null;
        }
    }

    public static String getNextOrderStatus(String orderStatus) {
        switch (orderStatus) {
            case Constants.OrderStatus.IN_PROGRESS:
                return Constants.OrderStatus.PLACED;
            case Constants.OrderStatus.PLACED:
                return Constants.OrderStatus.ACCEPTED;
            case Constants.OrderStatus.ACCEPTED:
                return Constants.OrderStatus.PROCESSING;
            case Constants.OrderStatus.PROCESSING:
                return Constants.OrderStatus.PROCESSED;
            case Constants.OrderStatus.PROCESSED:
                return Constants.OrderStatus.OUT_FOR_DELIVERY;
            case Constants.OrderStatus.OUT_FOR_DELIVERY:
                return Constants.OrderStatus.DELIVERED;
            default:
                return null;
        }
    }

    public static String getFormattedMoney(double moneyDouble) {
        BigDecimal bigDecimal = new BigDecimal(moneyDouble)
                .setScale(2, RoundingMode.HALF_UP);
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.CURRENCY_RUPEE_PREFIX).append(Constants.SPACE).append(bigDecimal.toString());
        return builder.toString();
    }

    public static String getFormattedMoneyWithoutCurrencyCode(double moneyDouble) {
        BigDecimal bigDecimal = new BigDecimal(moneyDouble)
                .setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.toString();
    }
}
