package com.commitstrip.commitstripreader.backend.schedule;

import com.commitstrip.commitstripreader.backend.config.SampleConfig;
import com.commitstrip.commitstripreader.backend.converter.StripDaoToSimpleStripDto;
import com.commitstrip.commitstripreader.backend.converter.StripDaoToStrip;
import com.commitstrip.commitstripreader.backend.dao.StripDao;
import com.commitstrip.commitstripreader.backend.repository.CommitStripRepository;
import com.commitstrip.commitstripreader.backend.repository.DatabaseRepository;
import com.commitstrip.commitstripreader.dto.NotificationDataPayload;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class FetchNewStripScheduledTasks {

    @Autowired
    private CommitStripRepository repositoryCommitStrip;

    @Autowired
    private DatabaseRepository repositoryDatabase;

    @Autowired
    private SampleConfig config;

    @Autowired
    private StripDaoToSimpleStripDto converterStripDaoToSimpleStripDto;

    private static final Logger log = LoggerFactory.getLogger(FetchNewStripScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Autowired
    public FetchNewStripScheduledTasks(CommitStripRepository repositoryCommitStrip, DatabaseRepository repositoryDatabase, SampleConfig config, StripDaoToSimpleStripDto converter) {
        this.repositoryCommitStrip = repositoryCommitStrip;
        this.repositoryDatabase = repositoryDatabase;
        this.converterStripDaoToSimpleStripDto = converter;
        this.config = config;
    }

    @Transactional
    @Scheduled(fixedDelay = 1800000) // Every 30 minutes
    public List<StripDao> fetchNewStrip() {
        List<StripDao> toSave = new ArrayList<StripDao>();

        try {
            Iterable<StripDao> strips = repositoryCommitStrip.fetchStripFromCommitStripOnPage(1);

            // Search which strip should we save
            for (StripDao stripDao : strips) {
               if (repositoryDatabase.findOneByTitle(stripDao.getTitle()) == null) {
                    toSave.add(stripDao);
               }
            }

            if (toSave.size() != 0) {
                // Sort by date
                toSave.sort((o1, o2) -> (o1.getDate().getTime() > o2.getDate().getTime() ? -1 : 1));

                // We need to override the following field: id, previous, next

                // We need to know the biggest id for not having the same id twice
                StripDao stripBiggestId= repositoryDatabase.findFirst1ByOrderByIdDesc();
                Long biggestId = stripBiggestId.getId();

                // We need to know the most recent strip id for the last previous field id
                StripDao stripMostRecent = repositoryDatabase.findFirst1ByOrderByDateDesc();
                Long previous = stripMostRecent.getId();

                for (int i=0; i<toSave.size(); i++) {
                    toSave.get(i).setId(biggestId+1+i); // The id is the biggest id + 1 + the current id

                    if (i == 0)
                        toSave.get(i).setNext(Long.valueOf(0)); // The first next is 0. It a default value.
                    else
                        toSave.get(i).setNext(toSave.get(i).getId() - 1); // The next in the general case, is the id less 1

                    if (i == toSave.size()-1)
                        toSave.get(i).setPrevious(previous); // If it the last of the collection we need to add the old first one
                    else
                        toSave.get(i).setPrevious(toSave.get(i).getId() + 1); // The previous is the current id + 1
                }

                // We can now register the new strip
                repositoryDatabase.save(toSave);

                // We need to update the last id
                stripMostRecent.setNext(biggestId + toSave.size());
                repositoryDatabase.save(stripMostRecent);

                // Send the notification
                StringBuilder json = new StringBuilder("{ \"to\": \"/topics/"+config.getNotificationTopic()+"\", \"data\": ");

                NotificationDataPayload notificationDataPayload = new NotificationDataPayload();
                for (StripDao strip: toSave) {
                    notificationDataPayload.getStrips().add(converterStripDaoToSimpleStripDto.convert(strip));
                }

                Gson gson = new Gson();
                json.append(gson.toJson(notificationDataPayload)).append("}");

                postNotification(config.getNotificationUrl(), json.toString());
            }

        } catch (IOException e) {
            log.error("Error when fetching first page on FetchNewStrip class", e);
        } catch (ParseException e) {
            log.error("Error when fetching first page on FetchNewStrip class", e);
        } catch (RuntimeException e) {
            log.error("Error when fetching first page on FetchNewStrip class", e);
        }

        return toSave;
    }

    private String postNotification(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "key="+config.getNotificationkey())
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
