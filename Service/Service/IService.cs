using Model.Domain;

namespace Service.Service;

public interface IService
{
    Referee Login(string username, string password, IRefereeObserver client);
    void Logout(string username, string password, IRefereeObserver client);
    List<Participant> GetParticipants(Referee referee);
    Result AddResult(Participant participant, Trial trial, int points);
    int GetTotalPointsAtTrial(Participant participant, Trial trial);
    List<Participant> GetParticipantsAtTrial(Trial trial);
}