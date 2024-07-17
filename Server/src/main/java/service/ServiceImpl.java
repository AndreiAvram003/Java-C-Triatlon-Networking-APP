package service;

import domain.Participant;
import domain.Referee;
import domain.Result;
import domain.Trial;
import org.example.IRefereeObserver;
import org.example.Service;
import repository.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServiceImpl implements Service {

    private final ParticipantRepository participantRepository;

    private final TrialRepository trialRepository;

    private final ResultRepository resultRepository;

    private final RefereeRepository refereeRepository;

    private Map<Long, IRefereeObserver> loggedReferees;

public ServiceImpl(ParticipantRepository participantRepository, TrialRepository trialRepository, ResultRepository resultRepository, RefereeRepository refereeRepository) {
        this.participantRepository = participantRepository;
        this.trialRepository = trialRepository;
        this.resultRepository = resultRepository;
        this.refereeRepository = refereeRepository;
        loggedReferees = new ConcurrentHashMap<>();
    }


    @Override
    public Referee login(String username, String password,IRefereeObserver client) throws Exception {
        Referee referee = refereeRepository.getByUsernameAndPassword(username,password);
        if(referee!=null){
            if(loggedReferees.get(referee.getId())!=null)
                throw new Exception("Referee already logged in");
            loggedReferees.put(referee.getId(),client);
        }
        return referee;
    }

    @Override
    public void logout(String username,String password, IRefereeObserver client) throws Exception {
    Referee referee = refereeRepository.getByUsernameAndPassword(username,password);
        System.out.println(referee.getId());
        IRefereeObserver localClient = loggedReferees.remove(referee.getId());
        if (localClient == null) {
            throw new Exception("Referee " + referee.getId() + " was not logged in");
        }
    }

    @Override
    public List<Participant>  getParticipants(Referee referee) {

        return participantRepository.getAllSorted();

    }

    private void notifyA(){
        for(IRefereeObserver o:loggedReferees.values())
            try{

                System.out.println("Trimit udpate la" + o.toString());
                o.update();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
    }

    @Override
    public Result addResult(Participant participant, Trial trial, int points){
        Result result = new Result(participant, trial, points);

        if (resultRepository.save(result).isPresent()) {
            notifyA();
            return result;

        }
        else{
            return null;
        }
    }

    @Override
    public int getTotalPointsAtTrial(Participant participant, Trial trial) {
        return resultRepository.getTotalPointsAtTrial(participant.getId(), trial.getId());
    }

    @Override
    public List<Participant> getParticipantsWithPointsAtTrial(Trial trial) {
        return resultRepository.getParticipantsWithPointsAtTrial(trial.getId());
    }


}
