package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import domain.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ParticipantDBRepo implements ParticipantRepository{

    private JdbcUtils dbUtils;



    private static final Logger logger= LogManager.getLogger();

    public ParticipantDBRepo(Properties props) {
        logger.info("Initializing ParticipantDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }
    @Override
    public Optional<Participant> save(Participant participant) {
        logger.traceEntry("saving participant {} ", participant);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into participants(name,points) values (?,?)")) {
            preStmt.setString(1, participant.getName());
            preStmt.setInt(2, participant.getPoints());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return Optional.of(participant);
    }

    @Override
    public Optional<Participant> getById(Long id) {
        logger.traceEntry("Getting participant with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("select * from participants where id = ?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String name = result.getString("name");
                    int points = result.getInt("points");
                    Participant participant = new Participant(id, name, points);
                    logger.trace("Found participant {}", participant);
                    return Optional.of(participant);
                } else {
                    logger.traceExit("Participant with id {} not found", id);
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
    public Optional<Participant> update(Participant participant) {
        logger.traceEntry("Updating participant {}", participant);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("UPDATE participants SET name = ?, points = ? WHERE id = ?")) {
            preStmt.setString(1, participant.getName());
            preStmt.setInt(2, participant.getPoints());
            preStmt.setLong(3, participant.getId());
            int result = preStmt.executeUpdate();
            if (result > 0) {
                logger.trace("Updated {} instances", result);
                logger.traceExit("Participant updated successfully: {}", participant);
                return Optional.of(participant);
            } else {
                logger.trace("No instances updated");
                logger.traceExit("Failed to update participant: {}", participant);
                return Optional.empty();
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return Optional.empty();
        }
    }


    @Override
    public Optional<Participant> deleteById(Long id) {
        logger.traceEntry("Deleting participant with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM participants WHERE id = ?")) {
            preStmt.setLong(1, id);
            int result = preStmt.executeUpdate();
            if (result > 0) {
                logger.trace("Deleted {} instances", result);
                logger.traceExit("Participant with id {} deleted successfully", id);
                return Optional.of(new Participant(id, null, null)); // Returning a placeholder Participant object
            } else {
                logger.trace("No instances deleted");
                logger.traceExit("Failed to delete participant with id {}", id);
                return Optional.empty();
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return Optional.empty();
        }
    }

    @Override
    public List<Participant> getAll() {
        logger.traceEntry("Getting all participants");
        List<Participant> participants = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM participants");
             ResultSet resultSet = preStmt.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Integer points = resultSet.getInt("points");
                Participant participant = new Participant(id, name, points);
                participants.add(participant);
            }
            logger.traceExit("Retrieved {} participants", participants.size());
            return participants;
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return participants;
        }
    }

    @Override
    public List<Participant> getAllSorted() {
        logger.traceEntry("Getting all participants");
        List<Participant> participants = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM participants ORDER BY name ASC");
             ResultSet resultSet = preStmt.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Integer points = resultSet.getInt("points");
                Participant participant = new Participant(id, name, points);
                participants.add(participant);
            }
            logger.traceExit("Retrieved {} participants", participants.size());
            return participants;
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
            return Collections.emptyList();
        }
    }




}
