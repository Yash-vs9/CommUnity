package com.Flow.Backend.controller;

import com.Flow.Backend.DTO.CreateEvent;
import com.Flow.Backend.DTO.EditEvent;
import com.Flow.Backend.DTO.EventDetailsDTO;
import com.Flow.Backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/createEvent")
    public String createEvent(@RequestBody CreateEvent createEvent){
        return eventService.createEvent(createEvent);
    }

    @PostMapping("/editEvent")
    public String editEvent(@RequestBody EditEvent editEvent){
        return eventService.editEvent(editEvent);
    }

    @DeleteMapping("/deleteEvent/{id}")
    public String deleteEvent(@PathVariable Long id){
        return eventService.deleteEvent(id);
    }
    @GetMapping("/joinEvent/{id}")
    public String joinEvent(@PathVariable Long id){
        return eventService.joinEvent(id);
    }
    @GetMapping("/latest5Events")
    public List<EventDetailsDTO> latest5EventsDetails(){
        return eventService.getLatestFiveEvents();
    }
    @GetMapping("/allEvents")
    public List<EventDetailsDTO> getAllEvents(){
        return eventService.getAllEvents();
    }
    @GetMapping("/allEventsOfCommunity/{id}")
    public List<EventDetailsDTO> getAllCommunityEvents(@PathVariable Long id){
        return eventService.getAllCommunitiesEvents(id);
    }
}
