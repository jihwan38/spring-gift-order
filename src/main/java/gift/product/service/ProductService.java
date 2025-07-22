package gift.product.service;

import gift.product.entity.Option;
import gift.product.dto.request.ProductCreateRequestDto;
import gift.product.dto.request.ProductRequestDto;
import gift.product.dto.response.ProductResponseDto;
import gift.product.entity.Product;
import gift.exception.ProductNotFoundException;
import gift.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public ProductResponseDto addProduct(ProductCreateRequestDto requestDto){
        Product product = new Product(
                requestDto.name(),
                requestDto.price(),
                requestDto.imageUrl(),
                requestDto.isKakaoApprovedByMd()
        );

        requestDto.optionRequestDtoList()
                .forEach(optionRequestDto -> {
                    Option option = new Option(
                            optionRequestDto.name(),
                            optionRequestDto.quantity());
                    product.addOption(option);
                });

        return ProductResponseDto.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(Pageable pageable){

        return productRepository.findAll(pageable).
                map(ProductResponseDto::from);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return ProductResponseDto.from(product);
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productToUpdate.updateProduct(
                requestDto.name(),
                requestDto.price(),
                requestDto.imageUrl(),
                requestDto.isKakaoApprovedByMd()
        );

        return ProductResponseDto.from(productToUpdate);
    }

    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
    }
}

