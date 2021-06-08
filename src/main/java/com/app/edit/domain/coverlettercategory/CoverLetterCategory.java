package com.app.edit.domain.coverlettercategory;

import com.app.edit.config.BaseEntity;
import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.domain.temporarycoverletter.TemporaryCoverLetter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Accessors(chain = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "cover_letter_category")
public class CoverLetterCategory extends BaseEntity {

    /*
     * 자소서 카테고리 ID 
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /*
     * 자소서 카테고리 이름
     **/
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @OneToMany(mappedBy = "coverLetterCategory", cascade = CascadeType.ALL)
    private List<CoverLetter> coverLetters;

    @OneToMany(mappedBy = "coverLetterCategory", cascade = CascadeType.ALL)
    private List<TemporaryCoverLetter> temporaryCoverLetters;

    public void addCoverLetter(CoverLetter coverLetter) {
        this.coverLetters.add(coverLetter);
        coverLetter.setCoverLetterCategory(this);
    }

    public void addTemporaryCoverLetter(TemporaryCoverLetter temporaryCoverLetter) {
        this.temporaryCoverLetters.add(temporaryCoverLetter);
        temporaryCoverLetter.setCoverLetterCategory(this);
    }

    @Builder
    public CoverLetterCategory(String name, List<CoverLetter> coverLetters, List<TemporaryCoverLetter> temporaryCoverLetters) {
        this.name = name;
        this.coverLetters = coverLetters;
        this.temporaryCoverLetters = temporaryCoverLetters;
    }
}
