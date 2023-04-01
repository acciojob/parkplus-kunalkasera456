package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
//Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
//Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
//If parkingLot is not found or no spot is available, throw "Cannot make reservation" exception.

        Reservation reservation = new Reservation();
        //chexk user present or not
        if(userRepository3.findById(userId) == null) {
            throw new Exception("Cannot make reservation");
        }

        User user = userRepository3.findById(userId).get();

        if(parkingLotRepository3.findById(parkingLotId) == null) {
            throw new Exception("Cannot make reservation");
        }

        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

        List<Spot> spotList = parkingLot.getSpotList();
        Spot optimalSpot = null;
        int optimalPrice = Integer.MAX_VALUE;

        for (Spot spot : spotList) {
            if(spot.getOccupied().equals(false)) { // check khali hai ki nahi
                if(spot.getSpotType() == SpotType.TWO_WHEELER) {
                    if (numberOfWheels <= 2) {
                        if(optimalPrice > spot.getPricePerHour()){
                            optimalPrice = spot.getPricePerHour();
                            optimalSpot = spot;
                        }
                    }
                }
                else if (spot.getSpotType() == SpotType.FOUR_WHEELER) {
                    if (numberOfWheels <= 4) {
                        if(optimalPrice > spot.getPricePerHour()){
                            optimalPrice = spot.getPricePerHour();
                            optimalSpot = spot;
                        }
                    }
                }
                else {
                    if(optimalPrice > spot.getPricePerHour()){
                        optimalPrice = spot.getPricePerHour();
                        optimalSpot = spot;
                    }
                }
            }
        }
        if (optimalSpot == null) {
            throw new Exception("Cannot make reservation");
        }

        optimalSpot.setOccupied(true);

        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(optimalSpot);

        user.getReservationList().add(reservation);

        userRepository3.save(user);

        return reservation;

    }
}
