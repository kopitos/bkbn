package com.gradle.hw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle.hw.model.Event;
import com.gradle.hw.model.User;
import com.gradle.hw.model.WeatherInfo;
import com.gradle.hw.model.WeatherUrl;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public Optional<User> getUsers(HttpServletRequest request) {

        User user = authenticate(request);
        return userRepository.findById(user.getId());
    }

    @GetMapping("/events")
    public List<Event> getEvents(HttpServletRequest request) {

        User user = authenticate(request);
        return eventRepository.findByUser(user);
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    @PostMapping("/events")
    public Event createEvent(@RequestBody Event event, HttpServletRequest request) throws JsonProcessingException {
        String desc = null;
        User user = authenticate(request);
        WeatherInfo weatherInfo = getWeather(event.getStart());
        desc = "The temperature on " + event.getStart() + " is: " + weatherInfo.getTemperature() + "K. And there is " + weatherInfo.getWeatherDescription();

        event.setWeather(desc);
        event.setUser(user);
        return eventRepository.save(event);
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
