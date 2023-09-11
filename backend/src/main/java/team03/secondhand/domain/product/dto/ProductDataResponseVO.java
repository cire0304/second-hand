package team03.secondhand.domain.product.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductDataResponseVO {
    private final String location;
    private final Long chatRoomCount;
    private final Integer watchListCount;
    private final Boolean isWatchListChecked;
    private final String productMainImgUrl;

    @Builder
    public ProductDataResponseVO(String location, Long chatRoomCount, Integer watchListCount, Boolean isWatchListChecked, String productMainImgUrl) {
        this.location = location;
        this.chatRoomCount = chatRoomCount;
        this.watchListCount = watchListCount;
        this.isWatchListChecked = isWatchListChecked;
        this.productMainImgUrl = productMainImgUrl;
    }
}
