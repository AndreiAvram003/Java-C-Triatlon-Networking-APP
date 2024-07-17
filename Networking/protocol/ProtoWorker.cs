using System.Net.Sockets;
using System.Text.RegularExpressions;
using Google.Protobuf;
using Model.Domain;
using Networking.DTO;
using Service.Service;
using TriatlonProto;
using RefereeDTO = Networking.DTO.RefereeDTO;
using TrialDTO = Networking.DTO.TrialDTO;

namespace Networking.protocol;

public class ProtoWorker : IRefereeObserver
{
    private IService server;
    private TcpClient connection;

    private NetworkStream stream;
    private volatile bool connected;

    public ProtoWorker(IService server, TcpClient connection)
    {
        this.server = server;
        this.connection = connection;
        try
        {
            stream = connection.GetStream();
            connected = true;
        }
        catch (Exception e)
        {
            Console.WriteLine(e.StackTrace);
        }
    }

    public void Run()
    {
        while (connected)
        {
            try
            {
                Console.WriteLine("Waiting requests ...");
                var request = TriatlonProto.Request.Parser.ParseDelimitedFrom(stream);
                Console.WriteLine("Request received: " + request);

                var response = HandleRequest(request);
                if (response != null)
                {
                    SendResponse(response);
                }
            }
            catch (IOException e)
            {
                Console.WriteLine("Error: " + e.Message);
                connected = false;
            }

            Thread.Sleep(1000);
        }

        stream.Close();
        connection.Close();
    }

    private TriatlonProto.Response HandleRequest(TriatlonProto.Request request)
    { 
        TriatlonProto.Response response = null;

            if (request.Type is TriatlonProto.Request.Types.Type.Login)
            {
                Console.WriteLine("Login request...");
                RefereeDTO refereeDto= ProtoUtils.getRefereeDTO(request);
                try
                {
                    lock (server)
                    {
                        Referee referee = server.Login(refereeDto.Password,refereeDto.Name, this);
                        RefereeDTO rdto = DTOUtils.GetDTO(referee);
                        return ProtoUtils.createOkResponse(rdto,rdto.TrialDTO);
                    }
                }
                catch (Exception e)
                {
                    connected = false;
                    return ProtoUtils.createErrorResponse(e.StackTrace);
                }
            }

            if (request.Type is TriatlonProto.Request.Types.Type.Logout)
            {
                Console.WriteLine("Logout request...");
                RefereeDTO refereeDto = ProtoUtils.getRefereeDTO(request);
                try
                {
                    lock (server)
                    {
                        server.Logout(refereeDto.Name,refereeDto.Password, this);
                    }
                    connected = false;
                    return ProtoUtils.createOkResponse(refereeDto,refereeDto.TrialDTO);
                }
                catch (Exception e)
                {
                    return ProtoUtils.createErrorResponse(e.Message);
                }
            }

            if (request.Type is TriatlonProto.Request.Types.Type.GetParticipants)
            {
                Console.WriteLine("Get participants request...");
                RefereeDTO refereeDto = ProtoUtils.getRefereeDTO(request);
                Referee referee = DTOUtils.GetFromDTO(refereeDto);
                try
                {
                    List<Participant> participants;
                    lock (server)
                    {
                        participants = server.GetParticipants(referee);
                        List<Networking.DTO.ParticipantDTO> participantDtos = DTOUtils.GetDTO(participants);
                        
                    }
                    return ProtoUtils.getParticipants(participants);
                } catch (Exception e)
                {
                    return ProtoUtils.createErrorResponse(e.Message);
                }
            }

            if (request.Type is TriatlonProto.Request.Types.Type.FilterParticipants)
            {
                Console.WriteLine("Filtered participants request");
                RefereeDTO refereeDto = ProtoUtils.getRefereeDTO(request);
                Referee referee = DTOUtils.GetFromDTO(refereeDto);
                try
                {
                    List<Participant> participants;
                    lock (server)
                    {
                        participants = server.GetParticipantsAtTrial(referee.trial);
                    }

                    return ProtoUtils.getParticipants(participants);

                } catch (Exception e)
                {
                    return ProtoUtils.createErrorResponse(e.Message);
                }
                
                
            }
            

            if (request.Type is TriatlonProto.Request.Types.Type.AddResult)
            {
                Console.WriteLine("Add result request...");
                Networking.DTO.ResultDTO resultDto = ProtoUtils.getResult(request);
                Trial trial = DTOUtils.GetFromDTO(resultDto.TrialDTO);
                Participant participant = DTOUtils.GetFromDTO(resultDto.ParticipantDTO);
                try
                {
                    lock (server)
                    {
                        server.AddResult(participant,trial,resultDto.Points);
                    }
                    return ProtoUtils.createOkResponse(resultDto);
                }
                catch (Exception e)
                {
                    return ProtoUtils.createErrorResponse(e.Message);
                }
            }

            if (request.Type is TriatlonProto.Request.Types.Type.PointsAtTrial)
            {
                Console.WriteLine("Get total points at trial request...");
                ParticipantTrialData participantTrialData = ProtoUtils.getParticipantTrialData(request);
                Participant participant = DTOUtils.GetFromDTO(participantTrialData.ParticipantDTO);
                Trial trial = DTOUtils.GetFromDTO(participantTrialData.TrialDTO);
                try
                {
                    int points;
                    lock (server)
                    {
                        points = server.GetTotalPointsAtTrial(participant,trial);
                    }
                    return ProtoUtils.createPointsResponse(points);
                }
                catch (Exception e)
                {
                    return ProtoUtils.createErrorResponse(e.Message);
                }
            }
            return response;
        }
    

    private void SendResponse(TriatlonProto.Response response)
    {
        Console.WriteLine("Sending response: " + response);
        response.WriteDelimitedTo(stream);
        stream.Flush();
    }

    public void Update()
    {
        SendResponse(ProtoUtils.resultAdded(null));
    }
}