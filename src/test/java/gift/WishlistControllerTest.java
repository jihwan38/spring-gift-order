package gift;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.token.TokenProvider;
import gift.product.entity.Product;
import gift.dto.PageDto;
import gift.wishlist.controller.WishlistController;
import gift.wishlist.dto.WishRequestDto;
import gift.wishlist.dto.WishResponseDto;
import gift.wishlist.entity.Wishlist;
import gift.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
public class WishlistControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WishlistService wishlistService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private MemberRepository memberRepository;

    private Member member = new Member(1L, "user@example.com", "salt", "password", "USER");
    private Product product1 = new Product(10L, "일반상품", 1000L, "http://image1.url", false);
    private Product product2 = new Product(20L, "카카오상품", 2000L, "http://image2.url", true);
    private Wishlist  wish1 = new Wishlist(1L, member, product1, 3);
    private Wishlist wish2 = new Wishlist(2L, member, product2, 1);
    private String testToken = "testToken";
    private Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        given(tokenProvider.isValidToken(any())).willReturn(true);
        given(tokenProvider.getMemberIdFromToken(any())).willReturn(member.getId());
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
    }

    @Test
    void 위시리스트에_상품_추가() throws Exception {
        var requestDto = new WishRequestDto(product1.getId(), 2);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        var responseDto = new WishResponseDto(3L,
                member.getId(),
                product1.getId(),
                product1.getName(),
                product1.getPrice(),
                product1.getImageUrl(),
                2);

        given(wishlistService.addWish(any(Member.class), any(WishRequestDto.class)))
                .willReturn(responseDto);

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/wishlist")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        WishResponseDto actualResponse = objectMapper.readValue(jsonResponse, WishResponseDto.class);

        assertThat(actualResponse.id()).isEqualTo(responseDto.id());
        assertThat(actualResponse.memberId()).isEqualTo(member.getId());
        assertThat(actualResponse.productId()).isEqualTo(product1.getId());
        assertThat(actualResponse.quantity()).isEqualTo(2);
        assertThat(actualResponse.name()).isEqualTo(product1.getName());
    }

    @Test
    void 위시리스트_조회__검증() throws Exception {
        var wishResponseDto1 = WishResponseDto.from(wish1);
        var wishResponseDto2 = WishResponseDto.from(wish2);

        List<WishResponseDto> content = Arrays.asList(wishResponseDto1, wishResponseDto2);


        Page<WishResponseDto> responsePage = new PageImpl<>(content, pageable, 2L);

        given(wishlistService.getWishesByMember(any(Member.class), any(Pageable.class))).willReturn(responsePage);

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/wishlist")
                        .header("Authorization", "Bearer " + testToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageDto.class, WishResponseDto.class);
        PageDto<WishResponseDto> actual = objectMapper.readValue(jsonResponse, type);

        assertThat(actual.totalElements()).isEqualTo(2);
        assertThat(actual.number()).isEqualTo(0);
        assertThat(actual.content()).hasSize(2)
                .extracting(WishResponseDto::id)
                .containsExactly(1L, 2L);
    }

    @Test
    void 위시리스트_삭제() throws Exception {
        Long wishId = 1L;
        doNothing().when(wishlistService).deleteWish(member, wishId);

        var response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/wishlist/{wishId}", wishId)
                        .header("Authorization", "Bearer " + testToken))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    void 인증_토큰_없이_위시리스트_추가_시_실패() throws Exception {
        var wishRequestDto = new WishRequestDto(1L, 2);
        var content = objectMapper.writeValueAsString(wishRequestDto);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void 인증_토큰_없이_위시리스트_조회_시_실패() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/wishlist"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void 인증_토큰_없이_위시리스트_삭제_시_실패() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/wishlist/1"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void 유효하지_않은_토큰으로_위시리스트_조회_시_실패() throws Exception {
        given(tokenProvider.isValidToken(any())).willReturn(false);

        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/wishlist")
                        .header("Authorization", "Bearer invalid-token"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(401);
    }
}