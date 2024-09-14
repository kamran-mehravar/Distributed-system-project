package com.carad.api.service;

import com.carad.api.ctrl.RestApiCtrl;
import com.carad.api.dto.AdvertisementDto;
import com.carad.api.dto.CarDto;
import com.ericsson.otp.erlang.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ErlangService {
    private static final Logger log = LoggerFactory.getLogger(ErlangService.class);
    @Value("${client}")
    private String client;

    @Value("${mnesia}")
    private String mnesia;

    @Value("${cookie}")
    private String cookie;

    @Value("${module}")
    private String module;


    private OtpConnection getConnection() throws IOException, OtpAuthException {
        OtpSelf self = null;
        OtpErlangObject received1 = null;
        self = new OtpSelf(client, cookie);
        OtpPeer peer = new OtpPeer(mnesia);
        OtpConnection connection = self.connect(peer);
        return connection;
    }

    public String getDate() {

        OtpErlangObject received1 = null;
        try {
            OtpConnection connection = getConnection();
            connection.sendRPC("erlang", "date", new OtpErlangList());
            received1 = connection.receiveRPC();
            System.out.println("Received value: " + received1.toString());
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (OtpAuthException e) {
            throw new RuntimeException(e);
        } catch (OtpErlangExit e) {
            throw new RuntimeException(e);
        }
        return received1.toString();
    }

    public AdvertisementDto getAdvertisements(String id) {
        AdvertisementDto ad = null;

        try {
            OtpConnection connection = getConnection();

            OtpErlangList nodeNameArg = new OtpErlangList(new OtpErlangAtom(id));
            connection.sendRPC(module, "getAdvertisements", nodeNameArg);

            OtpErlangObject received = connection.receiveRPC();

            if (received instanceof OtpErlangList) {
                OtpErlangList functionList = (OtpErlangList) received;
                List<AdvertisementDto> ads = convertToAdvertisementDtoList(functionList);
                if(!ads.isEmpty())
                    ad = ads.get(0);

            } else {
                log.info("Unexpected response: " + received.toString());
            }
            connection.close();
        } catch (IOException | OtpErlangExit | OtpAuthException e) {
            throw new RuntimeException(e);
        }

        return ad;
    }


    public List<AdvertisementDto> getAllAdvertisements() {
        List<AdvertisementDto> advertisements = null;


        try {
            OtpConnection connection = getConnection();

            OtpErlangList nodeNameArg = new OtpErlangList();
            connection.sendRPC(module, "getAllAdvertisements", nodeNameArg);

            OtpErlangObject received = connection.receiveRPC();

            if (received instanceof OtpErlangList) {
                OtpErlangList functionList = (OtpErlangList) received;
                advertisements = convertToAdvertisementDtoList(functionList);

            } else {
                System.out.println("Unexpected response: " + received.toString());
            }
            connection.close();
        } catch (IOException | OtpErlangExit | OtpAuthException e) {
            throw new RuntimeException(e);
        }

        return advertisements;
    }

    public String createAdvertisement(AdvertisementDto advertisementDto) {
        OtpErlangObject received = null;
        try {
            OtpConnection connection = getConnection();

            OtpErlangAtom idAtom = new OtpErlangAtom(advertisementDto.getId());
            OtpErlangAtom titleAtom = new OtpErlangAtom(advertisementDto.getTitle());
            OtpErlangAtom descriptionAtom = new OtpErlangAtom(advertisementDto.getDescription());
            OtpErlangAtom priceAtom = new OtpErlangAtom(advertisementDto.getPrice());
            OtpErlangAtom regionAtom = new OtpErlangAtom(advertisementDto.getRegion());
            OtpErlangAtom manufacturerAtom = new OtpErlangAtom(advertisementDto.getManufacturer());
            OtpErlangAtom productionYearAtom = new OtpErlangAtom(advertisementDto.getProductionYear());
            OtpErlangAtom usernameAtom = new OtpErlangAtom(advertisementDto.getUsername());
            OtpErlangAtom[] atoms = {idAtom, titleAtom, descriptionAtom, priceAtom, regionAtom, manufacturerAtom, productionYearAtom, usernameAtom};

            OtpErlangList nodeNameArg = new OtpErlangList(atoms);
            connection.sendRPC(module, "storeAdvertisement", nodeNameArg);
            received = connection.receiveRPC();
            System.out.println(received.toString());
            connection.close();
        } catch (IOException | OtpErlangExit | OtpAuthException e) {
            throw new RuntimeException(e);
        }

        return received.toString();
    }

    public String updateAdvertisement(String id, AdvertisementDto advertisementDto) {
        String received = null;
        AdvertisementDto dtos = getAdvertisements(id);
        if(dtos==null) {
            received = "No Such Record Found";
        }
        else {
            deleteAdvertisement(id);
            received = createAdvertisement(advertisementDto);
        }
        return received;
    }

    // Utility method to convert the Erlang list to a list of AdvertisementDto objects
    private List<AdvertisementDto> convertToAdvertisementDtoList(OtpErlangList erlangList) {
        List<AdvertisementDto> advertisements = new ArrayList<>();

        // Assuming the Erlang list contains nested tuples representing AdvertisementDto properties
        for (OtpErlangObject item : erlangList) {
            if (item instanceof OtpErlangTuple) {
                OtpErlangTuple tuple = (OtpErlangTuple) item;

                // Assuming the order of elements in the tuple matches the fields of AdvertisementDto
                OtpErlangObject idObj = tuple.elementAt(0);
                OtpErlangObject titleObj = tuple.elementAt(1);
                OtpErlangObject descriptionObj = tuple.elementAt(2);
                OtpErlangObject priceObj = tuple.elementAt(3);
                OtpErlangObject regionObj = tuple.elementAt(4);
                OtpErlangObject manufacturerObj = tuple.elementAt(5);
                OtpErlangObject productionYearObj = tuple.elementAt(6);
                OtpErlangObject usernameObj = tuple.elementAt(7);
                // Convert OtpErlangObjects to String (assuming the Erlang objects are strings)
                String id = idObj.toString();
                String title = titleObj.toString();
                String description = descriptionObj.toString();
                String price = priceObj.toString();
                String region = regionObj.toString();
                String manufacturer = manufacturerObj.toString();
                String productionYear = productionYearObj.toString();
                String username = usernameObj.toString();
                // Create AdvertisementDto object and add to the list
                AdvertisementDto advertisementDto = new AdvertisementDto(id, title, description, price, region, manufacturer, productionYear, username);
                advertisements.add(advertisementDto);
            } else {
                System.out.println("Unexpected item type in the Erlang list: " + item.getClass().getSimpleName());
            }
        }

        return advertisements;
    }

    public CarDto createCar(CarDto carDto) {
        List<CarDto> cars = null;

        try {
            OtpConnection connection = getConnection();

            OtpErlangAtom idAtom = new OtpErlangAtom(carDto.getId());
            OtpErlangAtom regionAtom = new OtpErlangAtom(carDto.getRegion());
            OtpErlangAtom priceAtom = new OtpErlangAtom(carDto.getPrice());
            OtpErlangAtom yearAtom = new OtpErlangAtom(carDto.getYear());
            OtpErlangAtom manufacturerAtom = new OtpErlangAtom(carDto.getManufacturer());
            OtpErlangAtom modelAtom = new OtpErlangAtom(carDto.getModel());
            OtpErlangAtom fuelAtom = new OtpErlangAtom(carDto.getFuel());
            OtpErlangAtom transmissionAtom = new OtpErlangAtom(carDto.getTransmission());
            OtpErlangAtom typeAtom = new OtpErlangAtom(carDto.getType());
            OtpErlangAtom paintColorAtom = new OtpErlangAtom(carDto.getPaint_color());
            OtpErlangAtom descriptionAtom = new OtpErlangAtom(carDto.getDescription());
            OtpErlangAtom postingDateAtom = new OtpErlangAtom(carDto.getPosting_date());
            OtpErlangAtom phoneAtom = new OtpErlangAtom(carDto.getPhone());

            OtpErlangAtom[] atoms = {idAtom, regionAtom, priceAtom, yearAtom, manufacturerAtom, modelAtom,
                    fuelAtom, transmissionAtom, typeAtom, paintColorAtom, descriptionAtom,
                    postingDateAtom, phoneAtom};

            OtpErlangList nodeNameArg = new OtpErlangList(atoms);
            connection.sendRPC(module, "storeCar", nodeNameArg);
            OtpErlangObject received = connection.receiveRPC();
            connection.close();
        } catch (IOException | OtpErlangExit | OtpAuthException e) {

            throw new RuntimeException(e);
        }

        return carDto;
    }
    private List<CarDto> convertToCarDtoList(OtpErlangList erlangList) {
        List<CarDto> cars = new ArrayList<>();

        // Assuming the Erlang list contains nested tuples representing CarDto properties
        for (OtpErlangObject item : erlangList) {
            if (item instanceof OtpErlangTuple) {
                OtpErlangTuple tuple = (OtpErlangTuple) item;

                // Assuming the order of elements in the tuple matches the fields of CarDto
                OtpErlangObject idObj = tuple.elementAt(0);
                OtpErlangObject regionObj = tuple.elementAt(1);
                OtpErlangObject priceObj = tuple.elementAt(2);
                OtpErlangObject yearObj = tuple.elementAt(3);
                OtpErlangObject manufacturerObj = tuple.elementAt(4);
                OtpErlangObject modelObj = tuple.elementAt(5);
                OtpErlangObject fuelObj = tuple.elementAt(6);
                OtpErlangObject transmissionObj = tuple.elementAt(7);
                OtpErlangObject typeObj = tuple.elementAt(8);
                OtpErlangObject paintColorObj = tuple.elementAt(9);
                OtpErlangObject descriptionObj = tuple.elementAt(10);
                OtpErlangObject postingDateObj = tuple.elementAt(11);
                OtpErlangObject phoneObj = tuple.elementAt(12);

                // Convert OtpErlangObjects to String (assuming the Erlang objects are strings)
                String id = idObj.toString();
                String region = regionObj.toString();
                String price = priceObj.toString();
                String year = yearObj.toString();
                String manufacturer = manufacturerObj.toString();
                String model = modelObj.toString();
                String fuel = fuelObj.toString();
                String transmission = transmissionObj.toString();
                String type = typeObj.toString();
                String paintColor = paintColorObj.toString();
                String description = descriptionObj.toString();
                String postingDate = postingDateObj.toString();
                String phone = phoneObj.toString();

                CarDto carDto = new CarDto(id, region, price, year, manufacturer, model, fuel, transmission,
                        type, paintColor, description, postingDate, phone);

                cars.add(carDto);
            } else {
                System.out.println("Unexpected item type in the Erlang list: " + item.getClass().getSimpleName());
            }
        }

        return cars;
    }
    public List<CarDto> getAllCars() {
        List<CarDto> cars = null;

        try {
            OtpConnection connection = getConnection();

            OtpErlangList nodeNameArg = new OtpErlangList();
            connection.sendRPC(module, "getAllCars", nodeNameArg);

            OtpErlangObject received = connection.receiveRPC();

            if (received instanceof OtpErlangList) {
                OtpErlangList functionList = (OtpErlangList) received;
                cars = convertToCarDtoList(functionList);
            } else {
                System.out.println("Unexpected response: " + received.toString());
            }

            connection.close();
        } catch (IOException | OtpErlangExit | OtpAuthException e) {
            throw new RuntimeException(e);
        }

        return cars;
    }


    public String deleteAdvertisement(String id) {
        log.info("Service:Deleting Advertisement with Id:"+id);
        String response = "SUCCESS : Record Deleted with id:"+id;
        if(getAdvertisements(id)==null){
           return "No Record Found : "+id;
        }
        OtpErlangObject received  = null;
        try {
            OtpConnection connection = getConnection();

            OtpErlangAtom idAtom = new OtpErlangAtom(id);
            OtpErlangAtom[] atoms = {idAtom};

            OtpErlangList nodeNameArg = new OtpErlangList(atoms);
            connection.sendRPC(module, "deleteAdvertisement", nodeNameArg);
            received = connection.receiveRPC();
            connection.close();
        } catch (IOException | OtpErlangExit | OtpAuthException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}