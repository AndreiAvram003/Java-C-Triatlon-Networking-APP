package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import domain.Participant;
import domain.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ResultDBRepo implements ResultRepository{

    private JdbcUtils dbUtils;



    private static final Logger logger= LogManager.getLogger();

    public ResultDBRepo(Properties props) {
        logger.info("Initializing ResultDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }
    @Override
    public Optional<Result> getById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Result> update(Result obj) {
        return Optional.empty();
    }

    @Override
    public Optional<Result> deleteById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Result> getAll() {
        return null;
    }

    @Override
    public Optional<Result> save(Result result) {
        logger.traceEntry("Saving result {} ", result);
        Connection con = dbUtils.getConnection();
        try{
            if(getResultForParticipant(con, result.getParticipant().getId(), result.getTrial().getId()) > 0){
                logger.traceExit("Result already exists");
                return Optional.empty();}
            else
                {
                    try (PreparedStatement preStmt = con.prepareStatement("UPDATE results SET result = ? WHERE participant_id = ? AND trial_id = ?", Statement.RETURN_GENERATED_KEYS)) {
                        preStmt.setLong(2, result.getParticipant().getId());
                        preStmt.setLong(3, result.getTrial().getId());
                        preStmt.setInt(1, result.getResult());
                        int resultStmt = preStmt.executeUpdate();
                        if (resultStmt > 0) {
                                logger.trace("Saved {} instances", result);
                                logger.traceExit("Result saved successfully: {}", result);
                                updateParticipantPoints(con, result.getParticipant().getId(), result.getResult());
                                return Optional.of(result);
                        }
                    } catch (SQLException ex) {
                        logger.error(ex);
                        System.err.println("Error DB " + ex);
                    }
                }
    } catch (SQLException ex) {
        logger.error(ex);
        System.err.println("Error DB " + ex);
    }
        logger.traceExit();
        return Optional.empty();
    }

    private int getResultForParticipant(Connection con, long participantId, long trialId) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT result FROM results WHERE participant_id = ? AND trial_id = ?")) {
            stmt.setLong(1, participantId);
            stmt.setLong(2, trialId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("result");
            }
        }
        return 0; // Returnăm 0 dacă nu există niciun rezultat pentru participantul și proba specificate
    }

    private void updateParticipantPoints(Connection con, Long participantId, int pointsToAdd) {
        try (PreparedStatement updateStmt = con.prepareStatement("UPDATE participants SET points = points + ? WHERE id = ?")) {
            updateStmt.setInt(1, pointsToAdd);
            updateStmt.setLong(2, participantId);
            int updateResult = updateStmt.executeUpdate();
            if (updateResult > 0) {
                logger.trace("Participant points updated successfully");
            } else {
                logger.trace("Failed to update participant points");
            }
        } catch (SQLException ex) {
            logger.error("Error updating participant points: {}", ex.getMessage());
        }
    }

    public int getTotalPointsAtTrial(Long participantId, Long trialId) {
        int totalPoints = 0;
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT result FROM results WHERE participant_id = ? AND trial_id = ?")) {
            preStmt.setLong(1, participantId);
            preStmt.setLong(2, trialId);
            ResultSet rs = preStmt.executeQuery();
            while (rs.next()) {
                int result = rs.getInt("result");
                totalPoints += result;
            }
        } catch (SQLException ex) {
            logger.error("Error fetching total points: {}", ex.getMessage());
        }
        return totalPoints;
    }


    public List<Participant> getParticipantsWithPointsAtTrial(Long trialId) {
        List<Participant> participants = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT DISTINCT p.* FROM participants p INNER JOIN results r ON p.id = r.participant_id WHERE r.trial_id = ?")) {
            preStmt.setLong(1, trialId);
            ResultSet rs = preStmt.executeQuery();
            while (rs.next()) {
                Long participantId = rs.getLong("id");
                String participantName = rs.getString("name");
                int participantPoints = rs.getInt("points");
                Participant participant = new Participant(participantId, participantName, participantPoints);
                participants.add(participant);
            }
        } catch (SQLException ex) {
            logger.error("Error fetching participants with points: {}", ex.getMessage());
        }
        return participants;
    }

}
