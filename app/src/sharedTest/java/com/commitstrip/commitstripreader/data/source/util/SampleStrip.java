package com.commitstrip.commitstripreader.data.source.util;

import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class SampleStrip {

    public static String STRIP_ID_ONE = "1";

    public static String STRIP_ID_ONE_TITLE = "Codeur Bohême";
    public static String STRIP_ID_TWO_TITLE = "True Story – L’excès de confiance";

    public static String JSON_FOR_STRIPS = "{" +
            "  \"content\":" +
            "[" +
            "{\"id\":"+STRIP_ID_ONE+",\"title\":\""+STRIP_ID_ONE_TITLE+"\",\"releaseDate\":\"2016-11-04\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/11/HeadlineImageTemplate-1-1.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/11/Strip-Le-remort-solidarité-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/11/04/solidarity-and-guilt/\",\"next\":0,\"previous\":2}," +
            "{\"id\":2,\"title\":\""+STRIP_ID_TWO_TITLE+"\",\"releaseDate\":\"2016-11-03\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/11/HeadlineImageTemplate-.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/11/Strip-Cacher-les-vieilles-technos-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/11/03/hide-this-code-that-i-dare-not-see/\",\"next\":1,\"previous\":3},{\"id\":3,\"title\":\"Codeur Bohême\",\"releaseDate\":\"2016-10-31\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/10/HeadlineImageTemplate-1-5.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Souvenirs-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/10/31/bohemian-coder/\",\"next\":2,\"previous\":4}," +
            "{\"id\":4,\"title\":\"Un remède magique\",\"releaseDate\":\"2016-10-27\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/10/HeadlineImageTemplate-1-4.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Chez-le-Psy-4-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/10/27/a-magical-cure/\",\"next\":3,\"previous\":5},{\"id\":5,\"title\":\"Pendant ce temps, sur Mars #11\",\"releaseDate\":\"2016-10-24\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/10/HeadlineImageTemplate-Récupéré.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Pendant-ce-temps-sur-Mars-11-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/10/24/meanwhile-on-mars-11/\",\"next\":4,\"previous\":6}," +
            "{\"id\":6,\"title\":\"Pour la science\",\"releaseDate\":\"2016-10-21\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/10/HeadlineImageTemplate-1-3.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Candy-Crush-650-final-1.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/10/21/for-science/\",\"next\":5,\"previous\":7}," +
            "{\"id\":7,\"title\":\"Ce bon vieux admin/password\",\"releaseDate\":\"2016-10-14\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/10/HeadlineImageTemplate-1-2.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Sécurité-Admin-password-final-1.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/10/14/good-old-adminpassword/\",\"next\":6,\"previous\":8}," +
            "{\"id\":8,\"title\":\"Une histoire d’unités CSS\",\"releaseDate\":\"2016-10-10\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/10/HeadlineImageTemplate-1-1.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-High-Level-CSS-650-final-2.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/10/10/a-story-about-css-units/\",\"next\":7,\"previous\":9}," +
            "{\"id\":9,\"title\":\"Témoignage de notre grande époque\",\"releaseDate\":\"2016-10-07\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/10/HeadlineImageTemplate-.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Dans-100-ans-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/10/07/timecapsule/\",\"next\":8,\"previous\":10}," +
            "{\"id\":10,\"title\":\"Mon royaume pour un commit\",\"releaseDate\":\"2016-09-30\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-8.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-SysAdmin-songeur-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/30/commit-or-die-trying/\",\"next\":9,\"previous\":11}," +
            "{\"id\":11,\"title\":\"Pendant ce temps, sur Mars – #10\",\"releaseDate\":\"2016-09-28\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-7.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Pendant-ce-temps-sur-Mars-10-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/28/meanwhile-on-mars-10/\",\"next\":10,\"previous\":12}," +
            "{\"id\":12,\"title\":\"La guerre invisible\",\"releaseDate\":\"2016-09-27\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-6.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Attaque-DDOS-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/27/the-invisible-war/\",\"next\":11,\"previous\":13}," +
            "{\"id\":13,\"title\":\"legacy != inutile\",\"releaseDate\":\"2016-09-26\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-5.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Obfusquer-son-code-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/26/legacy-useless/\",\"next\":12,\"previous\":14}," +
            "{\"id\":14,\"title\":\"A vouloir faire les malins…\",\"releaseDate\":\"2016-09-23\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-4.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Doucle-champs-mail-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/23/outsmarted/\",\"next\":13,\"previous\":15}," +
            "{\"id\":15,\"title\":\"Le contrôle des CTRLs\",\"releaseDate\":\"2016-09-22\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-3.jpg\",\"content\":\"https://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Sue-le-fil-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/22/ctrl-control/\",\"next\":14,\"previous\":16}," +
            "{\"id\":16,\"title\":\"Erreur de jeunesse\",\"releaseDate\":\"2016-09-09\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-2.jpg\",\"content\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Apprendre-a-etre-dev-650-final-1.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/09/the-mistakes-of-youth/\",\"next\":15,\"previous\":17}," +
            "{\"id\":17,\"title\":\"Y a-t-il une back-up dans l’avion ?\",\"releaseDate\":\"2016-09-05\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-1-1.jpg\",\"content\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Strip-Parachute-de-secours-inexistant-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/05/do-we-have-a-back-up-in-the-audience/\",\"next\":16,\"previous\":18}," +
            "{\"id\":18,\"title\":\"Keep it simple, stupid\",\"releaseDate\":\"2016-09-01\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/HeadlineImageTemplate-.jpg\",\"content\":\"http://www.commitstrip.com/wp-content/uploads/2016/09/Strip-Le-stagiaire-et-la-variable-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/09/01/keep-it-simple-stupid/\",\"next\":17,\"previous\":19},\n" +
            "{\"id\":19,\"title\":\"L’enfance du codeur : le mode sans échec\",\"releaseDate\":\"2016-08-29\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/08/HeadlineImageTemplate-1-3.jpg\",\"content\":\"http://www.commitstrip.com/wp-content/uploads/2016/08/Strip-Lenfance-du-codeur-Le-mode-sans-echec-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/08/29/childhood-of-a-coder-safe-mode/\",\"next\":18,\"previous\":20}," +
            "{\"id\":20,\"title\":\"Quand tu ne peux pas ne pas écouter\",\"releaseDate\":\"2016-08-23\",\"thumbnail\":\"http://www.commitstrip.com/wp-content/uploads/2016/08/HeadlineImageTemplate-1-2.jpg\",\"content\":\"http://www.commitstrip.com/wp-content/uploads/2016/08/Strip-Les-discussions-genantes-650-final.jpg\",\"url\":\"http://www.commitstrip.com/fr/2016/08/23/when-you-cant-not-listen/\", \"next\":19,\"previous\":21}" +
            "],\"last\":false,\"totalElements\":79,\"totalPages\":4,\"size\":20,\"number\":0,\"sort\":null,\"first\":true,\"numberOfElements\":20" +
            "}";

    public static Integer NUMBER_STRIPS_JSON = 20;

    public static StripDaoEntity generateSampleDao() {

        StripDaoEntity stripDao = new StripDaoEntity();
            stripDao.setId(1L);
            stripDao.setTitle("Codeur Bohême");
            stripDao.setIsFavorite(true);
            stripDao.setReleaseDate(new Date(1478371005));
            stripDao.setContent("https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Souvenirs-650-final.jpg");
            stripDao.setUrl("http://www.commitstrip.com/fr/2016/10/31/bohemian-coder/?");
            stripDao.setNext(null);
            stripDao.setPrevious(2L);

        return stripDao;
    }

    public static StripDto generateSampleDto() {

        StripDto stripDto = new StripDto();
        stripDto.setId(1L);
        stripDto.setTitle("Codeur Bohême");
        stripDto.setReleaseDate(new Date(1478371005));
        stripDto.setContent("https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Souvenirs-650-final.jpg");
        stripDto.setUrl("http://www.commitstrip.com/fr/2016/10/31/bohemian-coder/?");
        stripDto.setNext(null);
        stripDto.setPrevious(2L);

        return stripDto;
    }

    public static List<StripDto> generateSampleDto(long number) {
        List<StripDto> strips = new ArrayList<>();

        for (long i=0; i<number; i++){

            StripDto stripDto = new StripDto();
                stripDto.setId(i);
                stripDto.setTitle("Codeur Bohême "+i);
                stripDto.setReleaseDate(new Date(1478371005));
                stripDto.setContent("https://www.commitstrip.com/wp-content/uploads/2016/10/Strip-Souvenirs-650-final.jpg");
                stripDto.setUrl("http://www.commitstrip.com/fr/2016/10/31/bohemian-coder/?");
                stripDto.setNext(null);
                stripDto.setPrevious(2L);

            strips.add(stripDto);
        }

        return strips;
    }

    public static void compareEveryPropertiesOfStripDtoVsStripDao (StripDto source, StripDaoEntity other) {

        assertTrue(source.getId().equals(other.getId()));
        assertTrue(source.getTitle().equals(other.getTitle()));
        assertTrue(source.getReleaseDate().equals(other.getReleaseDate()));
        assertTrue(source.getContent().equals(other.getContent()));
        assertTrue(source.getUrl().equals(other.getUrl()));

        if (source.getNext() != null)
            assertTrue(source.getNext().equals(other.getNext()));
        else
            assertTrue(other.getNext() == null);

        if (source.getPrevious() != null)
            assertTrue(source.getPrevious().equals(other.getPrevious()));
        else
            assertTrue(other.getPrevious() == null);

    }

    public static void compareEveryPropertiesOfStripDto (StripDto source, StripDto other) {

        assertTrue(source.getId().equals(other.getId()));
        assertTrue(source.getTitle().equals(other.getTitle()));
        assertTrue(source.getContent().equals(other.getContent()));
        assertTrue(source.getUrl().equals(other.getUrl()));

        if (source.getNext() != null)
            assertTrue(source.getNext().equals(other.getNext()));
        else
            assertTrue(other.getNext() == null);

        if (source.getPrevious() != null)
            assertTrue(source.getPrevious().equals(other.getPrevious()));
        else
            assertTrue(other.getPrevious() == null);

    }

    public static Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        return gsonBuilder.create();
    }
}
