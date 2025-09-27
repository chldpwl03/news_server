package com.example.hello.news.service;

import com.example.hello.news.dto.CategoryDTO;
import com.example.hello.news.dto.NewsResponse;
import com.example.hello.news.dto.SourceDTO;
import com.example.hello.news.dto.SourceResponse;
import com.example.hello.news.entity.Category;
import com.example.hello.news.entity.Source;
import com.example.hello.news.repository.CategoryRepository;
import com.example.hello.news.repository.SourceRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class NewsService {
    @Value("${newsapi.source_url}")
    private String sourceURL;
    @Value("${newsapi.article_url}")
    private String artcleurl;
    @Value("${newsapi.apiKey}")
    private String apikey;

    private final CategoryRepository categoryRepository;
    private final SourceRepository sourceRepository;
//@Autowired
//private CategoryRepository categoryRepository; 위의 코드와 같은 코드

    public NewsResponse getGeneral() throws URISyntaxException, IOException, InterruptedException {
        String url="https://newsapi.org/v2/top-headlines?country=us&apiKey=bec0599ef1bd4c30bef83d66b9af9511";

        //client instance를 생성한다
        HttpClient client= HttpClient.newBuilder().build();

        //request 인스턴스를 생성한다(필수: url,method(요청방법))
        HttpRequest request= HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        //client에서 request를 보내고 response형테로 받아온다
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String resBody= response.body();

        Gson gson=new Gson();
        NewsResponse newsResponse= gson.fromJson(resBody,NewsResponse.class);
        System.out.println(newsResponse.getStatus());

        return newsResponse;
    }

    public List<CategoryDTO> getCategories(){
        //categoryRepository.findAll(); == select * from category; >>> fetch
        List<Category> categories= categoryRepository.findAll();

        //비어있는 category dto 리스트 인스턴스를 생성한다
        List<CategoryDTO> categoryDTOList=new ArrayList<>();
        for(Category category: categories){
            CategoryDTO dto=new CategoryDTO();
            dto.setName(category.getName());
            dto.setMemo(category.getMemo());
            categoryDTOList.add(dto);
        }
        return categoryDTOList;
    }

    public String inputCategory(Category category) {
        if (category !=null){
            try{
                Category saved= categoryRepository.save(category);
//                saved.getName().equals(category.getName());
            }catch (Exception e){
                return String.format("error: %s",e.getMessage());
            }

            return "SUCCESS";
        }

        return "error:카테고리 정보가 없습니다";
    }

    public void inputSources() throws URISyntaxException, IOException, InterruptedException {
        String url = sourceURL + apikey;
        System.out.println(url);


        //client instance를 생성한다
        HttpClient client = HttpClient.newBuilder().build();

        //request 인스턴스를 생성한다(필수: url,method(요청방법))
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        //client에서 request를 보내고 response형테로 받아온다
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String resBody = response.body();

        Gson gson = new Gson();

        SourceResponse sourceResponse= gson.fromJson(resBody, SourceResponse.class);

        System.out.println(sourceResponse.getStatus());
        System.out.println(sourceResponse.getSources().length);

        //sourceResponse에 있는 모든 SourceDTO 인스턴스의 데이터를 이용하여
        //Source Entity 인스턴스를 생성하고 데이터베이스에 저장한다
        try {
            for (SourceDTO dto : sourceResponse.getSources()) {
                Source source = new Source(); //빈 source entity 인스턴스를 생성
                source.setSid(dto.getId());
                source.setName(dto.getName());
                source.setDescription(dto.getDescription());
                source.setUrl(dto.getUrl());
                source.setLanguage(dto.getLanguage());
                source.setCountry(dto.getCountry());
                source.setCategory(dto.getCategory());
                sourceRepository.save(source);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
    public List<SourceDTO> getSources(){
        //데이터 베이스로부터 Source entity 리스트를 가져와서
        //모든 Source entity 인스턴스를 SourceDTO인스턴스로 반환하여 반환한다
        List<Source> sources=sourceRepository.findAll();

//        for (Source source:sources){} 이거랑 아래의 두 개는 같은 문장
        //stream().foreach(Funtional interface -> 익명클래스 -> 람다식)
        //stream().map(Funtional interface -> 익명클래스 -> 람다식)
        //foreach는 반환값이 없다,

        //map(source

        return sources.stream().map(Source::toDTO).toList();
    }

    public void inputArticles(String category) throws URISyntaxException, IOException, InterruptedException {
        String url=String.format("%scategory=%s&%s",artcleurl,category,apikey);

        System.out.println(url);
        //client instance를 생성한다
        HttpClient client = HttpClient.newBuilder().build();

        //request 인스턴스를 생성한다(필수: url,method(요청방법))
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        //client에서 request를 보내고 response형테로 받아온다
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String resBody = response.body();

        Gson gson = new Gson();
        NewsResponse newsResponse= gson.fromJson(resBody,NewsResponse.class);
        System.out.println(newsResponse.getStatus());
        System.out.println(newsResponse.getTotalResults());
    }


}
