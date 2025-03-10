package team03.secondhand.domain.ChatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.seller.memberId = :memberId OR cr.buyer.memberId = :memberId")
    List<ChatRoom> findByMemberId(@Param("memberId") Long memberId);

    Optional<ChatRoom> findByChatroomId(Long roomId);

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE (cr.seller.memberId = :memberId OR cr.buyer.memberId = :memberId) AND cr.product.productId = :productId")
    Optional<ChatRoom> findByMemberIdAndProductId(@Param("memberId") Long memberId, @Param("productId") Long productId);

    @Query("SELECT count(*) From ChatRoom cr " +
            "WHERE cr.product.productId = :productId")
    Long getChatRoomCountBy(@Param("productId") Long productId);

    @Query("SELECT cr From ChatRoom cr " +
            "WHERE cr.product.productId in (:productId)")
    List<ChatRoom> getChatRoomsCountBy(@Param("productId") List<Long> productId);
    
}
