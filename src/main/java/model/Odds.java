package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "event_odds")
public class Odds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double bookieA_1_odds;
    private double bookieA_0_odds;
    private double bookieA_2_odds;
    private double bookieB_1_odds;
    private double bookieB_0_odds;
    private double bookieB_2_odds;
    private double bookieC_1_odds;
    private double bookieC_0_odds;
    private double bookieC_2_odds;
    private double bookieD_1_odds;
    private double bookieD_0_odds;
    private double bookieD_2_odds;

}
