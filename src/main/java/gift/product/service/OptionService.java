package gift.product.service;

import gift.exception.ProductNotFoundException;
import gift.product.dto.request.OptionRequestDto;
import gift.product.dto.response.OptionResponseDto;
import gift.product.entity.Option;
import gift.product.repository.OptionRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OptionService {
    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OptionResponseDto addOption(Long productId, OptionRequestDto optionRequestDto) {
        Product product = getProduct(productId);

        Option option = new Option(optionRequestDto.name(),
                optionRequestDto.quantity());

        product.addOption(option);

        return OptionResponseDto.from(optionRepository.save(option));
    }

    public List<OptionResponseDto> getOptions(Long productId) {
        Product product = getProduct(productId);

        return product.getOptions()
                .stream()
                .map(OptionResponseDto::from)
                .toList();
    }

    @Transactional
    public OptionResponseDto updateOption(Long productId, Long optionId, OptionRequestDto optionRequestDto) {
        Product product = getProduct(productId);

        Option option = product.getOptionByOptionId(optionId);

        product.validateOptionForUpdate(optionRequestDto.name(), optionId);

        option.updateOption(optionRequestDto.name(), optionRequestDto.quantity());

        return OptionResponseDto.from(option);
    }

    @Transactional
    public void decreaseOptionQuantity(Long productId, Long optionId, int quantityToDecrease) {
        Product product = getProduct(productId);

        product.decreaseOptionQuantity(optionId, quantityToDecrease);
    }

    @Transactional
    public void deleteOption(Long productId, Long optionId) {
        Product product = getProduct(productId);

        product.removeOptionByOptionId(optionId);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
