package org.example;

import domain.Participant;
import domain.Referee;
import domain.Result;
import domain.Trial;

import java.util.List;

public interface Service {
    Referee login(String username, String password, IRefereeObserver client) throws Exception;
    void logout(String username, String password, IRefereeObserver client) throws Exception;
    List<Participant> getParticipants(Referee refere) throws Exception;

    Result addResult(Participant participant, Trial trial, int points) throws Exception;

    int getTotalPointsAtTrial(Participant participant, Trial trial) throws Exception;

    List<Participant> getParticipantsWithPointsAtTrial(Trial trial) throws Exception;



}
