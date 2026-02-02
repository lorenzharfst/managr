
package dev.lorenzharfst.managr.objects.club;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClubController {

    @Autowired
    ClubService clubService;

    @PostMapping("/new")
    long createClub(@RequestParam String name, @RequestParam String description) {
        return clubService.createClub(name, description);
    }

    @PutMapping("/join")
    void joinClub(@RequestParam String memberUsername, @RequestParam long clubId) {
        clubService.joinClub(memberUsername, clubId);
    }
}
