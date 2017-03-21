package com.commitstrip.commitstripreader.backend.repository;

import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.backend.service.DownloadFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Repository that fetch synchronously or asynchronously strips from CommitStrip website.
 */
@Repository
public class CommitStripRepositoryImpl implements CommitStripRepository {

    private static final java.lang.String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private String URL_BASE = "http://www.commitstrip.com/fr/";

    @Autowired
    private DownloadFile downloadFile;

    public CommitStripRepositoryImpl () {
        downloadFile = new DownloadFile();
    }

    /**
     * Fetch synchronously the number of page on the CommitStrip website.
     *
     * @return Number of page on the CommitStrip website
     * @throws IOException
     */
    public Integer fetchPageNumber () throws IOException {

        String content = downloadFile.downloadFileFromNetwork("http://www.commitstrip.com/fr/");

        Document jsoup = Jsoup.parse(content);

        String pageNumber = jsoup.select("div#content > nav ")
                                .get(0)
                                .select("nav.nav-main > div.wp-pagenavi > span.pages")
                                .get(0)
                                .text().replaceAll("Page 1 sur ", "");

        return Integer.parseInt(pageNumber);
    }

    @Override
    public Iterable<StripDao> fetchStripFromCommitStripOnPage(Integer page) throws IOException, ParseException {

        if (page < 0 || page > fetchPageNumber()) {
            throw new IllegalArgumentException("Page number must be between 1 and "+fetchPageNumber());
        }

        List<StripDao> toReturn = new ArrayList<>();

        String url = URL_BASE;
        url += page == 1 ? "" : "page/" + page + "/";

        String content = downloadFile.downloadFileFromNetwork(url);

        Document document = Jsoup.parse(content);
        Elements all_strips = document.select("div#content > .excerpts > .excerpt");

        StripDao strip; Document documentSecondPage; Elements strip_element;
        String dateWithTimezone; DateFormat dateFormat; boolean isStrip; int previous, next;

        int id;
        if (page == 1)
            id = page;
        else
            id = ((page-1) * 20)+1;

        for (Element content_strips : all_strips) {

            isStrip = true;

            strip = new StripDao();
            strip.setId(Long.valueOf(id));
            strip.setTitle(content_strips.select("section > a > span > strong").text());

            Elements thumbnail = content_strips.select("section > a > img");
            if (thumbnail.size() > 0)
                strip.setThumbnail(thumbnail.get(0).attr("src"));

            strip.setUrl(content_strips.select("section > a ").get(0).attr("href"));

            documentSecondPage = Jsoup.parse(downloadFile.downloadFileFromNetwork(strip.getUrl()));
            strip_element = documentSecondPage.select("article");

            Elements strips_element = strip_element.select("div > p > img");
            if (strips_element.size() > 0 || strip_element.select("img.size-full").size() > 0) {
                String src = "";
                if (strips_element.size() > 0)
                    src = strips_element.get(0).attr("src");
                else
                    src = strip_element.select("img.size-full").get(0).attr("src");

                if (src.startsWith("//")) { src = "http:" + src; }
                strip.setContent(src);
            }
            else {
                isStrip = false;
            }

            dateWithTimezone = strip_element.select("header > div > a > time").get(0).attr("datetime");
            String date = dateWithTimezone.substring(0, dateWithTimezone.length() - 6);

            dateFormat = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT, Locale.ENGLISH);
            strip.setReleaseDate(dateFormat.parse(date));

            previous = id+1;
            next = id-1;

            strip.setPrevious(Long.valueOf(previous));
            strip.setNext(Long.valueOf(next));

            if (isStrip) {
                toReturn.add(strip);
                id++;
            }
        }

        return toReturn;
    }

    public void setDownloadFile(DownloadFile downloadFile) {
        this.downloadFile = downloadFile;
    }
}
