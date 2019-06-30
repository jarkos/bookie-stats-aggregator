package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sport_event")
@NamedQuery(name = "findSportEvents", query = "from model.SportEvent se")
@Getter
@Setter
@ToString
public class SportEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String flashScoreEventId;
    private String league;
    private Date date;
    private String firstTeam;
    private String secondTeam;
    private int firstTeamResult;
    private int secondTeamResult;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "odds_id")
    private Odds odds;
}
