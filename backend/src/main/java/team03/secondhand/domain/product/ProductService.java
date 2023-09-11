package team03.secondhand.domain.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team03.secondhand.domain.ChatRoom.ChatRoomRepository;
import team03.secondhand.domain.category.Category;
import team03.secondhand.domain.category.CategoryRepository;
import team03.secondhand.domain.location.Location;
import team03.secondhand.domain.location.LocationRepository;
import team03.secondhand.domain.member.Member;
import team03.secondhand.domain.member.MemberRepository;
import team03.secondhand.domain.product.dto.ProductDataRequestDTO;
import team03.secondhand.domain.product.dto.ProductDataResponseDTO;
import team03.secondhand.domain.product.dto.query.ProductDetailDTO;
import team03.secondhand.error.MemberError;
import team03.secondhand.error.ProductError;
import team03.secondhand.util.imageUpload.ImageUploadModule;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final ImageUploadModule imageUploadModule;

    /**
     * Public Method
     */

    public ProductDataResponseDTO.SimpleInfo createProduct(Long memberId, ProductDataRequestDTO request) {
        if (memberId == 0L) {
            throw new MemberError.InvalidGuest();
        }

        // Get Info
        Category category = categoryRepository.getReferenceById(request.getCategoryId());
        Location location = locationRepository.getReferenceById(request.getLocationId());
        Member member = memberRepository.getReferenceById(memberId);

        // Create Product
        Product product = Product.builder()
                .title(request.getTitle())
                .price(request.getPrice())
                .content(request.getContent())
                .category(category)
                .location(location)
                .member(member)
                .build();
        Product savedProduct = productRepository.save(product);

        // Upload Product Images
        uploadProductImages(request.getProductImageUrls(), savedProduct);

        return new ProductDataResponseDTO.SimpleInfo(savedProduct);
    }

    public ProductDetailDTO getDetailProductBy(Long memberId, Long productId) {
        Product product = productQueryRepository.getDetailProductBy(productId)
                .orElseThrow(ProductError.NotFoundProduct::new);
        product.incrementLookupCount();

        Long chatRoomCount = chatRoomRepository.getChatRoomCountBy(productId);

        return new ProductDetailDTO(memberId, product, chatRoomCount);
    }

    public boolean deleteProductBy(Long memberId, Long productId) {
        Product product = productRepository
                .findProductByProductId(productId)
                .orElseThrow(ProductError.NotFoundProduct::new);

        if (product.isSoldBy(memberId)) {
            throw new IllegalArgumentException("본인 판매글이 아닙니다.");
        }

        productRepository.delete(product);
        return true;
    }

    public void updateSalesStatus(Long memberId, Long productId, String newStatus) {
        Product product = productRepository
                .findProductByProductId(productId)
                .orElseThrow(ProductError.NotFoundProduct::new);

        if (product.isSoldBy(memberId)) {
            throw new IllegalArgumentException("본인 판매글이 아닙니다.(상태 수정)");
        }

        ProductState productStatus;
        if (newStatus.equals("판매중")) {
            productStatus = ProductState.판매중;
        } else if (newStatus.equals("예약중")) {
            productStatus = ProductState.예약중;
        } else if (newStatus.equals("판매완료")) {
            productStatus = ProductState.판매완료;
        } else {
            throw new IllegalArgumentException("해당하는 상태는 존재하지 않습니다.");
        }

        product.updateSalesStatus(productStatus);
        productRepository.save(product);
    }

    /**
     * Private Method
     */

    private void uploadProductImages(List<MultipartFile> multipartFiles, Product product) {
        List<String> imageUrls = new ArrayList<>();
        int indexNum = 0;
        for (MultipartFile multipartFile : multipartFiles) {

            indexNum += 1;
            String imgName = product.getProductId() + "-" + indexNum; // 파일 이름(물품ID + index)
            String imagePath = imageUploadModule.productImageUpload(multipartFile, imgName);
            imageUrls.add(imagePath);
        }
        product.changeImages(imageUrls);
    }

}
