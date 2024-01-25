package team03.secondhand.domain.member.model;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.secondhand.domain.location.Location;
import team03.secondhand.domain.member.Member;
import team03.secondhand.domain.memberAndLocation.MemberAndLocation;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MemberAndLocations {

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MemberAndLocation> memberAndLocationList = new ArrayList<>();

    public MemberAndLocations(Member member, List<Location> locations) {
        memberAndLocationList.addAll(locations.stream()
                .map(location -> new MemberAndLocation(member, location))
                .collect(Collectors.toList()));
        changeMainLocation();
    }

    public void changeLocation(List<Location> locations, List<Long> locationOrders) {
        Member member = memberAndLocationList.get(0).getMember();
        memberAndLocationList.clear();
        List<Location> sortedLocations = sortByRequestOrder(locations, locationOrders);
        memberAndLocationList.addAll(sortedLocations.stream()
                .map(location -> new MemberAndLocation(member, location))
                .collect(Collectors.toList()));
        changeMainLocation();
    }

    private List<Location> sortByRequestOrder(List<Location> locations, List<Long> locationOrders) {
        List<Location> updatedLocation = new ArrayList<>();
        for (Long locationOrder : locationOrders) {
            for (Location location : locations) {
                if (Objects.equals(locationOrder, location.getLocationId())) {
                    updatedLocation.add(location);
                    break;
                }
            }
        }
        return updatedLocation;
    }

    private void changeMainLocation() {
        memberAndLocationList.get(0).setMainLocationState();
    }

    public List<MemberAndLocation> getMemberAndLocationList() {
        return memberAndLocationList;
    }

}
