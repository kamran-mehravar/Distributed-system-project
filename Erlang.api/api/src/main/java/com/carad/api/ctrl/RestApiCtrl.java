package com.carad.api.ctrl;

import com.carad.api.dto.AdvertisementDto;
import com.carad.api.dto.CarDto;
import com.carad.api.service.ErlangService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RestApiCtrl {
    private static final Logger log = LoggerFactory.getLogger(RestApiCtrl.class);
    @Autowired
    ErlangService erlangService;

    @GetMapping("/advertisements")
    public ResponseEntity<List<AdvertisementDto>> getAllAdvertisements() {
        log.info("Returning All Avertisements >>>>>>> ");
        List<AdvertisementDto> ads = erlangService.getAllAdvertisements();
        if (ads.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            // Return 200 OK with the list of advertisements
            return ResponseEntity.ok(ads);
        }
    }

    @PutMapping("/advertisements/{id}")
    public ResponseEntity<String> updateAdvertisement(@PathVariable("id") String id, @RequestBody AdvertisementDto advertisement) {
        String ads = erlangService.updateAdvertisement(id, advertisement);
        if (ads==null) {
            // Return 404 Not Found if the list is emptycd
            return ResponseEntity.notFound().build();
        } else {
            // Return 200 OK with the list of advertisements
            return ResponseEntity.ok(ads);
        }
    }

    @DeleteMapping("/advertisements/{id}")
    public ResponseEntity<String> deleteAdvertisement(@PathVariable("id") String id) {
        log.info("CTRL:Deleting Advertisement with Id:"+id);
      String ads  = erlangService.deleteAdvertisement(id);
        if (ads==null) {
            // Return 404 Not Found if the list is emptycd
            return ResponseEntity.notFound().build();
        } else {
            // Return 200 OK with the list of advertisements
            return ResponseEntity.ok(ads);
        }
    }

    @GetMapping("/searchAdvertisementsByTitle")
    public ResponseEntity<AdvertisementDto> searchAdvertiesment(@RequestParam("title") String title) {
        AdvertisementDto ads = erlangService.getAdvertisements(title);
        if (ads==null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(ads);
        }
    }

    @PostMapping("/advertisements")
    public ResponseEntity<String> createAdvertisement(@RequestBody AdvertisementDto dto) {
        String ads = erlangService.createAdvertisement(dto);
        if (ads==null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(ads);
        }
    }

    @GetMapping("/advertisements/{id}")
    public ResponseEntity<AdvertisementDto> getAdvertisementById(@PathVariable("id") String id) {
        AdvertisementDto ads = erlangService.getAdvertisements(id);
        if (ads==null) {
            return ResponseEntity.noContent().build();
        } else {
            // Return 200 OK with the list of advertisements
            return ResponseEntity.ok(ads);
        }
    }

    @GetMapping("/date")
    public ResponseEntity<String> getDate() {
        String date = erlangService.getDate();
        return ResponseEntity.ok(date);
    }

    @PostMapping("/createCar")
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto dto) {
        CarDto cars = erlangService.createCar(dto);
        if (cars==null) {
            // Return 404 Not Found if the list is emptycd
            return ResponseEntity.notFound().build();
        } else {
            // Return 200 OK with the list of advertisements
            return ResponseEntity.ok(cars);
        }
    }

    @GetMapping("/allCars")
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> cars = erlangService.getAllCars();
        if (cars.isEmpty()) {
            // Return 404 Not Found if the list is emptycd
            return ResponseEntity.notFound().build();
        } else {
            // Return 200 OK with the list of advertisements
            return ResponseEntity.ok(cars);
        }
    }
}
