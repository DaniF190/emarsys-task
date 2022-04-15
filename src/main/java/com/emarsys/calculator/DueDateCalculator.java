package com.emarsys.calculator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DueDateCalculator {

    private static final int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private DueDateCalculator () {}

    public static Date calculateDueDate (Date date, int turnaroundTime) throws IllegalArgumentException, ParseException {

        var year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        var month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
        var day = Integer.parseInt(new SimpleDateFormat("dd").format(date));

        var hour = Integer.parseInt(new SimpleDateFormat("HH").format(date));
        var min = Integer.parseInt(new SimpleDateFormat("mm").format(date));

        var dayOfTheWeek = calculateTheDayOfTheWeek(year, month, day);

        var timeInMin = hour * 60 + min;
        if (timeInMin < 9 * 60 || timeInMin > 17 * 60 || dayOfTheWeek == 0 || dayOfTheWeek == 6)
            throw new IllegalArgumentException("The given date is not between working hours!");

        //---- Main logic starts here ----//

        var turnAroundTimeInMin = turnaroundTime * 60;

        /* Subtracting the difference between the starting time of the day and the end of that day
        from the turnaround time. */

        turnAroundTimeInMin -= (17 * 60 - timeInMin);

        //Exchanging the remaining time to day/hour/min

        int remMin = 0;
        int remHour = 0;
        int remDay = 0;
        if (turnAroundTimeInMin > 59) {

            remHour = turnAroundTimeInMin / 60;
            remMin = turnAroundTimeInMin % 60;

            if (remHour > 7) {

                remDay = (remHour / 8) + 1;
                remHour = remHour % 8;
            }
        }

        if (remDay == 0) {

            return getDate(year, month, day, remHour, remMin);
        }

        return recursiveDateSearch(year, month, day, remDay, remHour, remMin);

        //---- Main logic ends here ----//
    }

    /*
    * In this method I am using the calculated remaining days to step through my calendar
    * each time taking away a day but only if it's a working day and if It founds the date then return it, otherwise
    * if the loop gets to the end of the year then the method calls itself with a new year.
    */
    private static Date recursiveDateSearch (int year, int month, int day, int remDay, int remHour, int remMin)
            throws ParseException {

        var isLeapYear = isLeapYear(year);

        for (int i = 0; i < daysInMonths.length; i++) {

            if (i == 0 && isLeapYear) daysInMonths[1] = 29;
            else if (i == 0) daysInMonths[1] = 28;


            for (int j = 0; j < daysInMonths[i]; j++) {

                var dayOfWeek = calculateTheDayOfTheWeek(year, i + 1, j + 1);
                if ((dayOfWeek != 0 && dayOfWeek != 6) &&
                        ((i + 1 == month && j + 1 > day) || i + 1 > month)) remDay--;

                if (remDay == 0) {

                    return getDate(year, i + 1, j + 1, remHour, remMin);
                }
            }

            if (i == 11) {

                recursiveDateSearch(year + 1, 0, 0, remDay, remHour, remMin);
            }
        }
        return null;
    }

    private static Date getDate(int year, int month, int day, int remHour, int remMin) throws ParseException {
        Date returnDate;
        var dateString = String.format("%s-%s-%s %s:%s", year, month, day, 9 + remHour, remMin);
        returnDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
        return returnDate;
    }

    //Here iam using the Zeller's rule for calculating the day of the week
    private static int calculateTheDayOfTheWeek (int year, int month, int day) {

        if (month < 3) {
            year -= 1;
            month += 10;
        } else {
            month -= 2;
        }

        var tmpYear = Integer.toString(year);

        var fstHalf = Integer.parseInt(tmpYear.substring(0,2));
        var sndHalf = Integer.parseInt(tmpYear.substring(2));

        var returnValue = day + ((13 * month - 1) / 5) + sndHalf + (sndHalf / 4) + (fstHalf / 4) - 2 * fstHalf;

        return returnValue % 7;
    }

    private static boolean isLeapYear (int year) {

        if (year % 100 == 0) return (year % 4000 == 0);

        return (year % 4 == 0);
    }
}
