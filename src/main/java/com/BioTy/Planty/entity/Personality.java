package com.BioTy.Planty.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Personality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private String emoji;
    private String color;
    private String description;
    private String exampleComment;

    public Personality(String label, String emoji, String color){
        this.label = label;
        this.emoji = emoji;
        this.color = color;
    }

}
