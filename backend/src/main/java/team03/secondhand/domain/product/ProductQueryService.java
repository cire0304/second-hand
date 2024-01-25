package team03.secondhand.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import team03.secondhand.domain.ChatRoom.ChatRoom;
import team03.secondhand.domain.ChatRoom.ChatRoomRepository;
import team03.secondhand.domain.product.dto.query.ProductInfoDTO;
import team03.secondhand.domain.product.vo.ProductSearchCondition;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {
    private final ProductQueryRepository productQueryRepository;
    private final ChatRoomRepository chatRoomRepository;


    public List<ProductInfoDTO> getProductsBy(Long memberId, ProductSearchCondition condition, Pageable pageable) {
        List<Product> products = productQueryRepository.getProductsBy(condition, pageable);

        Map<Long, Long> chatRoomsCountMap = getChatRoomsCountMapBy(products);

        return toProductInfoDTO(memberId, products, chatRoomsCountMap);
    }

    public List<ProductInfoDTO> getMemberProductsBy(Long memberId, ProductSearchCondition condition, Pageable pageable) {
        List<Product> products = productQueryRepository.getMemberProductsBy(memberId, condition, pageable);

        Map<Long, Long> chatRoomsCountMap = getChatRoomsCountMapBy(products);

        return toProductInfoDTO(memberId, products, chatRoomsCountMap);
    }

    public List<ProductInfoDTO> getWatchProductsBy(Long memberId, Pageable pageable) {
        List<Product> products = productQueryRepository.getWatchProductsBy(memberId, pageable);

        Map<Long, Long> chatRoomsCountMap = getChatRoomsCountMapBy(products);

        return toProductInfoDTO(memberId, products, chatRoomsCountMap);
    }

    private Map<Long, Long> getChatRoomsCountMapBy(List<Product> products) {
        List<ChatRoom> chatRooms = chatRoomRepository.getChatRoomsCountBy(
                products.stream()
                        .map(Product::getProductId)
                        .collect(Collectors.toList())
        );
        Map<Long, Long> chatRoomsCountMap = new HashMap<>();
        for (ChatRoom chatRoom : chatRooms) {
            Long count = chatRoomsCountMap.getOrDefault(chatRoom.getProduct().getProductId(), 0L);
            chatRoomsCountMap.put(chatRoom.getProduct().getProductId(), count + 1);
        }

        for (Product product : products) {
            chatRoomsCountMap.putIfAbsent(product.getProductId(), 0L);
        }

        return chatRoomsCountMap;
    }

    private List<ProductInfoDTO> toProductInfoDTO(Long memberId, List<Product> products, Map<Long, Long> chatRoomsCountMap) {
        return products.stream()
                .map(product -> new ProductInfoDTO(memberId, product, chatRoomsCountMap.get(product.getProductId())))
                .collect(Collectors.toList());
    }
}
