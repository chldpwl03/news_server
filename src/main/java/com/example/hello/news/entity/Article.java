package com.example.hello.news.entity;

import com.example.hello.news.dto.ArticleDTO;
import com.example.hello.news.dto.SourceDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Article {
    public static SourceDTO toDTO(Source source){
        SourceDTO dto=new SourceDTO();
        dto.setId(source.getSid());
        dto.setName(source.getName());
        dto.setDescription(source.getDescription());
        dto.setCategory(source.getCategory());
        dto.setUrl(source.getUrl());
        dto.setLanguage(source.getLanguage());
        dto.setCountry(source.getCountry());
        return dto;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source",foreignKey = @ForeignKey(name = "article_ibfk_1"))
    private Source source;
    @JoinColumn(name = "category",foreignKey = @ForeignKey(name = "article_ibfk_2"))
    private Category category;

    @Column(length = 50)
    private String author;


    @Column(length = 50)
    private String title;


    @Column (columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String url;


    @Column(name="url_To_Image", length = 500)
    private String urlToImage;

    @Column(name = "published_at",insertable = false)
    private String publishedAt;

    @Column(columnDefinition = "TEXT")
    private String content;

    public static Article fromDTO(ArticleDTO dto, Source src,Category cat){
        Article article=new Article();

        article.setSource(src);
        article.setCategory(cat);
        article.setAuthor(dto.getAuthor());
        article.setTitle(dto.getTitle());
        article.setDescription(dto.getDescription());
        article.setUrl(dto.getUrl());
        article.setUrlToImage(dto.getUrlToImage());
        article.setPublishedAt(dto.getPublishedAt());
        article.setContent(dto.getContent());

        return article;
    }






}
