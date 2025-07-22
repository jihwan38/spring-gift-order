package gift.product.controller;


import gift.product.dto.request.ProductCreateRequestDto;
import gift.product.dto.request.ProductRequestDto;
import gift.product.dto.response.ProductResponseDto;
import gift.product.dto.view.ProductFormDto;
import gift.product.dto.view.ProductView;
import gift.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/products")
public class ProductAdminController {
    private final ProductService productService;


    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model,
                               @PageableDefault(sort = "id")
                               Pageable pageable) {
        Page<ProductView> productViews = productService.getProducts(pageable)
                        .map(ProductView::from);

        model.addAttribute("products", productViews);

        return "admin/products";
    }

    @GetMapping("/add")
    public String showAddForm(Model model){
        model.addAttribute("product",
                ProductFormDto.emptyForm());

        return "admin/add-form";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") ProductCreateRequestDto productRequestDto,
                             BindingResult bindingResult,
                             Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("product", productRequestDto);
            return "admin/add-form";
        }

        productService.addProduct(productRequestDto);

        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        ProductResponseDto productDto = productService.getProduct(id);

        ProductFormDto productFormDto = ProductFormDto.from(productDto);

        model.addAttribute("product", productFormDto);
        model.addAttribute("productId", id);

        return "admin/edit-form";
    }

    @PutMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") ProductRequestDto requestDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "admin/edit-form";
        }

        productService.updateProduct(id, requestDto);

        return "redirect:/admin/products";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        return "redirect:/admin/products";
    }


}
