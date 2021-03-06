package services;

import models.Trip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

@Service
public class TripServiceImpl implements TripService {

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    public Trip getTripById(Integer id) {
        if (id == null || id < 1 || id == Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        return em.find(Trip.class, id);
    }

    @Transactional
    public Integer save(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException();
        }
        Set<Trip> tripsAll = getAllStoredTrips();
        if (tripsAll.stream().filter(storedTrip -> storedTrip.getLocation().equals(trip.getLocation())
                                     && storedTrip.getStart() == trip.getStart()).count() > 0) {
            throw new IllegalArgumentException("Date is already booked");
        }

        em.persist(trip);
        logger.debug("Trip to location {} for the duration of {} to {} and id {} saved to db.", trip.getLocation(), trip.getStart(), trip.getEnd(), trip.getId());
        return trip.getId();
    }

    @Transactional


    public Set<Trip> getAllStoredTrips() {
        CriteriaQuery<Trip> query = em.getCriteriaBuilder().createQuery(Trip.class);
        query.from(Trip.class);
        List<Trip> trips = em.createQuery(query).getResultList();
        Set<Trip> tripsSet = new HashSet<Trip>(trips);

        return tripsSet;
    }

}