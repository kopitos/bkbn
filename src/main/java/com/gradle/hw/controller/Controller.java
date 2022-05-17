package com.gradle.hw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle.hw.model.*;
import com.gradle.hw.repository.EventRepository;
import com.gradle.hw.repository.UserRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private WeatherUrl weatherData;

    @Autowired
    RestTemplate restTemp;

    @GetMapping("/users")
    public ResponseEntity<UserInfo> getUsers(HttpServletRequest request) {

        UserInfo userInfo = new UserInfo();
        User user = authenticate(request);
        userInfo.setEmail(user.getEmail());
        userInfo.setName(user.getFirstName()+ " " + user.getLastName());
        //return userRepository.findById(user.getId());
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventInfo>> getEvents(HttpServletRequest request) {

        User user = authenticate(request);
        List<EventInfo> eventInfoList = new ArrayList<EventInfo>();
        List<Event> event = eventRepository.findByUser(user);
        for (Event e : event) {
            EventInfo eventInfo = new EventInfo(e.getTitle(), e.getStart(), e.getEnd(), e.getDescription(), e.getWeather());
            eventInfoList.add(eventInfo);
        }

        return ResponseEntity.ok(eventInfoList);
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return ResponseEntity.created(URI.create("")).body("User is created successfully!");
    }

    @PostMapping("/events")
    public ResponseEntity<String> createEvent(@RequestBody Event event, HttpServletRequest request) throws JsonProcessingException {
        String desc = null;
        User user = authenticate(request);
        //long unixTime = event.getStart().getTime() / 1000L;
        WeatherInfo weatherInfo = getWeather(event.getStart());
        desc = "The temperature on " + event.getStart() + " is: " + weatherInfo.getTemperature() + "K. And there is " + weatherInfo.getWeatherDescription();

        event.setWeather(desc);
        event.setUser(user);
        eventRepository.save(event);

        return ResponseEntity.created(URI.create("")).body("Event is created successfully!");
    }

    private User authenticate(HttpServletRequest request){
        String upd = request.getHeader("authorization");
        String pair=new String(Base64.decodeBase64(upd.substring(6)));
        String userName=pair.split(":")[0];
        return userRepository.findByEmail(userName);
    }

    public WeatherInfo getWeather(LocalDate date) throws JsonMappingException, JsonProcessingException {
        String city = "Aachen";
        UriComponents uriComponents = UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host(weatherData.getUrl())
                .path("")
                .query("q="+city+"&date="+date+"&appid="+weatherData.getApiKey())
                .build();

        String uri = uriComponents.toUriString();
        ResponseEntity<String> resp= restTemp.exchange(uri, HttpMethod.GET, null, String.class);

        ObjectMapper mapper = new ObjectMapper();
        WeatherInfo weather = mapper.readValue(resp.getBody(), WeatherInfo.class);
        return weather;
    }
}
