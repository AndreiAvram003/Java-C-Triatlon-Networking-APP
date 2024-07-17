namespace Networking.DTO;

[Serializable]
public class RefereeDTO
{
    public long Id { get; set; }
    public string Password { get; set; }
    public TrialDTO TrialDTO { get; set; }
    public string Name { get; set; }

    public RefereeDTO(long id, string password, string name, TrialDTO trialDTO)
    {
        Id = id;
        Password = password;
        Name = name;
        TrialDTO = trialDTO;
    }
}