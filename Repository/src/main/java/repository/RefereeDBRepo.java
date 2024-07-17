package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import domain.Participant;
import domain.Referee;
import domain.Trial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class RefereeDBRepo implements RefereeRepository {

    private JdbcUtils dbUtils;


    private static final Logger logger = LogManager.getLogger();

    public RefereeDBRepo(Properties props) {
        logger.info("Initializing RefereeDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional<Referee> save(Referee referee) {
        logger.traceEntry("Saving referee {} ", referee);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO referees(name, password, trial_id) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preStmt.setString(1, referee.getName());
            preStmt.setString(2, referee.getPassword());
            preStmt.setLong(3, referee.getTrial().getId());
            int result = preStmt.executeUpdate();
            if (result > 0) {
                ResultSet generatedKeys = preStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    referee.setId(id);
                    logger.trace("Saved {} instances", result);
                    logger.traceExit("Referee saved successfully: {}", referee);
                    return Optional.of(referee);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Referee> getById(Long id) {
        logger.traceEntry("Getting referee with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT referees.id, referees.name, referees.password, referees.trial_id, trials.name as trial_name FROM referees INNER JOIN trials ON referees.trial_id = trials.id WHERE referees.id = ?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    String password = result.getString("password");
                    Long trialId = result.getLong("trial_id");
                    String trialName = result.getString("trial_name");
                    List<Participant> participants = getParticipantsForTrialRef(trialId);

                    Trial trial = new Trial(trialId,null, participants, trialName);
                    Referee referee = new Referee(id, name, password, trial);
                    trial.setReferee(referee);
                    logger.trace("Found referee {}", referee);
                    return Optional.of(referee);
                } else {
                    logger.traceExit("Referee with id {} not found", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return Optional.empty();
        }
    }
    @Override
    public Referee getByUsernameAndPassword(String username,String password) {
        logger.traceEntry("Getting referee with username and password {}", username,password);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT referees.id, referees.name, referees.password, referees.trial_id, trials.name AS trial_name FROM referees INNER JOIN trials ON referees.trial_id = trials.id WHERE referees.name = ? and referees.password = ?")) {
            preStmt.setString(1, username);
            preStmt.setString(2,password);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id = result.getLong("id");
                    Long trialId = result.getLong("trial_id");
                    String trialName = result.getString("trial_name");
                    List<Participant> participants = getParticipantsForTrialRef(trialId);

                    Trial trial = new Trial(trialId,null, participants, trialName);
                    Referee referee = new Referee(id, username, password, trial);
                    trial.setReferee(referee);
                    logger.trace("Found referee {}", referee);
                    return referee;
                } else {
                    logger.traceExit("Referee with username and password {} not found", username);
                    return null;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return null;
        }
    }


    private List<Participant> getParticipantsForTrialRef(Long trialId) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        try (Connection con = dbUtils.getNewConnection();
             PreparedStatement preStmt = con.prepareStatement("SELECT p.* FROM participants p " +
                     "INNER JOIN results res ON p.id = res.participant_id " +
                     "WHERE res.trial_id = ?")) {
            preStmt.setLong(1, trialId);
            try (ResultSet resultSet = preStmt.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    Integer points = resultSet.getInt("points");
                    participants.add(new Participant(id, name, points));
                }
            }
        }
        return participants;
    }

    @Override
    public Optional<Referee> update(Referee referee) {
        logger.traceEntry("Updating referee {}", referee);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("UPDATE referees SET name = ?, password = ?, trial_id = ? WHERE id = ?")) {
            preStmt.setString(1, referee.getName());
            preStmt.setString(2, referee.getPassword());
            preStmt.setLong(3, referee.getTrial().getId());
            preStmt.setLong(4, referee.getId());
            int result = preStmt.executeUpdate();
            if (result > 0) {
                logger.trace("Updated {} instances", result);
                logger.traceExit("Referee updated successfully: {}", referee);
                return Optional.of(referee);
            } else {
                logger.trace("No instances updated");
                logger.traceExit("Failed to update referee: {}", referee);
                return Optional.empty();
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Referee> deleteById(Long id) {
        logger.traceEntry("Deleting referee with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM referees WHERE id = ?")) {
            preStmt.setLong(1, id);
            int result = preStmt.executeUpdate();
            if (result > 0) {
                logger.trace("Deleted {} instances", result);
                logger.traceExit("Referee with id {} deleted successfully", id);
                return Optional.of(new Referee(id,null,null,null)); // Returning a placeholder Referee object
            } else {
                logger.trace("No instances deleted");
                logger.traceExit("Failed to delete referee with id {}", id);
                return Optional.empty();
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return Optional.empty();
        }
    }

    public List<Referee> getAll() {
        logger.traceEntry("Getting all referees");
        List<Referee> referees = new ArrayList<>();
        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement("SELECT referees.id, referees.name, referees.password, referees.trial_id, trials.name AS trial_name FROM referees INNER JOIN trials ON referees.trial_id = trials.id");
             ResultSet resultSet = preStmt.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                Long trialId = resultSet.getLong("trial_id");
                String trialName = resultSet.getString("trial_name");
                List<Participant> participants = getParticipantsForTrialRef(trialId);

                Trial trial = new Trial(trialId, null, participants, trialName);
                Referee referee = new Referee(id, name, password, trial);
                trial.setReferee(referee);
                referees.add(referee);
            }
            logger.traceExit("Retrieved {} referees", referees.size());
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        return referees;
    }

}
