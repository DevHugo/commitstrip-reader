package com.commitstrip.commitstripreader.integrationtest.util;

import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.dto.StripDto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SampleData {

    public static List<StripDao> addSampleStrips (int number) {
        List<StripDao> strips = new ArrayList<>();

        DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = sourceFormat.parse("2016-09-30");
        } catch (ParseException e) {}

        StripDao stripOne = new StripDao(Long.valueOf(1), "Mon royaume pour un commit", date,
                "http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-8.jpg",
                "https://www.commitstrip.com/wp-content/uploads/2016/09/StripDto-SysAdmin-songeur-650-final.jpg",
                "http://www.commitstrip.com/fr/2016/09/30/commit-or-die-trying/");

        stripOne.setNext(Long.parseLong("0"));
        stripOne.setPrevious(Long.parseLong("2"));

        try {
            date = sourceFormat.parse("2016-09-28");
        } catch (ParseException e) {}

        StripDao stripTwo = new StripDao(Long.valueOf(2), "Pendant ce temps, sur Mars – #10", date,
                "http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-7.jpg",
                "https://www.commitstrip.com/wp-content/uploads/2016/09/StripDto-Pendant-ce-temps-sur-Mars-10-650-final.jpg",
                "http://www.commitstrip.com/fr/2016/09/28/meanwhile-on-mars-10/");

        stripTwo.setNext(Long.parseLong("1"));
        stripTwo.setPrevious(Long.parseLong("3"));

        try {
            date = sourceFormat.parse("2016-09-27");
        } catch (ParseException e) {}

        StripDao stripThree = new StripDao(Long.valueOf(3), "La guerre invisible", date,
                "http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-6.jpg",
                "https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Attaque-DDOS-650-finalenglish.jpg",
                "http://www.commitstrip.com/en/2016/09/27/the-invisible-war/");

        stripThree.setNext(Long.parseLong("2"));
        stripThree.setPrevious(Long.parseLong("4"));

        if (number >= 1)
            strips.add(stripOne);

        if (number >= 2)
            strips.add(stripTwo);

        if (number == 3)
            strips.add(stripThree);

        if (number >= 4) {
            for (int i = 0; i < number-3; i++) {
                strips.add(new StripDao(Long.valueOf(i), "Strip numéro "+i, date,
                        "http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-6.jpg",
                        "https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Attaque-DDOS-650-finalenglish.jpg",
                        "http://www.commitstrip.com/en/2016/09/27/the-invisible-war/"));
            }
        }

        return strips;
    }
}
