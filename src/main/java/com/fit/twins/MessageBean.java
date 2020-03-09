package com.fit.twins;

import com.google.gson.Gson;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.httpcore.ICoreAuthenticationProvider;
import com.fit.twins.demo.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MessageBean implements Serializable {


    public String getMessage(Object token, Object postLink, List<String> winners) throws IOException {
        OAuth2AuthenticationDetails token2 = (OAuth2AuthenticationDetails) token;
        ICoreAuthenticationProvider authenticationProvider = request -> request;

        OkHttpClient client = HttpClients.createDefault(authenticationProvider);
        Gson gson = new Gson();


        UserInfo userInfo = getUserInfo(token2, client, gson);
        InstagramBAID instagramBAID = getInstagramBusinessAccountId(token2, client, gson, userInfo);
        Post post = getPostByLink((String) postLink, token2, client, gson, instagramBAID);
        List<IgComments.IgComment2> data = getallPostComments(token2, client, gson, post);

        List<IgComments.IgComment2> uniqUsers = data.stream()
                .filter(distinctByKey(IgComments.IgComment2::getUsername))
                .filter(it -> !winners.contains(it.getUsername()))
                .collect(Collectors.toList());

        Collections.shuffle(uniqUsers);
        int size = uniqUsers.size();
        Random rand = new Random();
        int index = rand.nextInt(size);
        IgComments.IgComment2 igComment2 = uniqUsers.get(index);

        return igComment2.getUsername() +  " когда отметила " + igComment2.getText();
    }

    private List<IgComments.IgComment2> getallPostComments(OAuth2AuthenticationDetails token2, OkHttpClient client, Gson gson, Post post) throws IOException {
        Request request;
        Response response;
        request = new Request.Builder()
                .url("https://graph.facebook.com/v5.0/" + post.getId() + "?fields=comments%7Busername%2Ctext%7D%2Ccomments_count&access_token=" +
                        token2.getTokenValue())
                .build();
        response = client.newCall(request).execute();


        IgMedia2 igMedia2 = gson.fromJson(response.body().string(), IgMedia2.class);

        String next2 = igMedia2.getComments().getPaging() == null ? null : igMedia2.getComments().getPaging().getNext();
        while (next2 != null && !next2.isEmpty()) {
            request = new Request.Builder()
                    .url(next2)
                    .build();
            response = client.newCall(request).execute();

            IgComments igComments = gson.fromJson(response.body().string(), IgComments.class);
            igMedia2.getComments().getData().addAll(igComments.getData());
            next2 = igComments.getPaging() == null ? null : igComments.getPaging().getNext();

        }
        return igMedia2.getComments().getData();
    }

    private Post getPostByLink(String postLink, OAuth2AuthenticationDetails token2, OkHttpClient client, Gson gson, InstagramBAID instagramBAID) throws IOException {
        Request request;
        Response response;
        request = new Request.Builder()
                .url("https://graph.facebook.com/v5.0/" + instagramBAID.getInstagram_business_account().getId() + "/media?fields=id%2Cshortcode&access_token="
                        + token2.getTokenValue())
                .build();
        response = client.newCall(request).execute();


        Data allPosts = gson.fromJson(response.body().string(), Data.class);
        String[] split = postLink.split("/");
        String s1 = split[4];

        Post post = allPosts.getData().stream()
                .filter(s -> s.getShortcode().equalsIgnoreCase(s1))
                .findAny()
                .orElse(null);

        String next = allPosts.getPaging().getNext();
        while (post == null) {
            request = new Request.Builder()
                    .url(next)
                    .build();
            response = client.newCall(request).execute();

            Data allPosts2 = gson.fromJson(response.body().string(), Data.class);
            allPosts.getData().addAll(allPosts.getData());
            post = allPosts.getData().stream().filter(s -> s.getShortcode().equalsIgnoreCase(s1))
                    .findAny()
                    .orElse(null);
            next = allPosts2.getPaging() == null ? null : allPosts.getPaging().getNext();

        }
        return post;
    }

    private InstagramBAID getInstagramBusinessAccountId(OAuth2AuthenticationDetails token2, OkHttpClient client, Gson gson, UserInfo userInfo) throws IOException {
        Request request;
        Response response;
        request = new Request.Builder()
                .url("https://graph.facebook.com/v5.0/" + userInfo.getData().get(0).getId() + "?fields=instagram_business_account&access_token="
                        + token2.getTokenValue())
                .build();
        response = client.newCall(request).execute();

        return gson.fromJson(response.body().string(), InstagramBAID.class);
    }

    private UserInfo getUserInfo(OAuth2AuthenticationDetails token2, OkHttpClient client, Gson gson) throws IOException {
        Request request = new Request.Builder()
                .url("https://graph.facebook.com/v5.0/me/accounts?access_token="
                        + token2.getTokenValue())
                .build();

        Response response = client.newCall(request).execute();

        return gson.fromJson(response.body().string(), UserInfo.class);
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}