package com.devterin.controller;

import com.devterin.dtos.dto.AttributeTypeDTO;
import com.devterin.dtos.dto.AttributeValueDTO;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.service.impl.AttributeTypeServiceImpl;
import com.devterin.service.impl.AttributeValueServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attribute")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeTypeServiceImpl attributeTypeService;
    private final AttributeValueServiceImpl attributeValueService;

    @PostMapping("/type")
    ApiResponse<AttributeTypeDTO> addAttributeType(@RequestBody AttributeTypeDTO request) {
        return ApiResponse.<AttributeTypeDTO>builder()
                .result(attributeTypeService.addAttributeType(request.getName(),
                        request.getDescription())).build();
    }

    @GetMapping("/type")
    ApiResponse<List<AttributeTypeDTO>> getAllAttributeTypes() {

        return ApiResponse.<List<AttributeTypeDTO>>builder()
                .result(attributeTypeService.getAllAttributeTypes()).build();
    }

    @GetMapping("/type/{attributeTypeId}")
    public ApiResponse<AttributeTypeDTO> getAttributeTypeById(@PathVariable Long attributeTypeId) {
        return ApiResponse.<AttributeTypeDTO>builder()
                .result(attributeTypeService.getAttributeTypeById(attributeTypeId)).build();
    }

    @PutMapping("/type/{attributeTypeId}")
    public ApiResponse<AttributeTypeDTO> updateAttributeType(@PathVariable Long attributeTypeId,
                                                             @RequestBody AttributeTypeDTO request) {
        return ApiResponse.<AttributeTypeDTO>builder()
                .result(attributeTypeService.updateAttributeType(attributeTypeId,
                        request.getName(), request.getDescription())).build();
    }

    @DeleteMapping("/type/{attributeTypeId}")
    public ApiResponse<Void> deleteAttributeTypeById(@PathVariable Long attributeTypeId) {
        attributeTypeService.deleteAttributeType(attributeTypeId);

        return ApiResponse.<Void>builder()
                .message("Attribute Type Deleted").build();
    }

    @PostMapping("/value")
    ApiResponse<AttributeValueDTO> addAttribute(@RequestBody AttributeTypeDTO request) {

        return ApiResponse.<AttributeValueDTO>builder()
                .result(attributeValueService.addAttributeValue(request.getName(),
                        request.getAttributeTypeId())).build();
    }


//    @GetMapping("/value/{attributeId}")
//    public ApiResponse<AttributeValueDTO> getAttributeById(@PathVariable Long attributeId) {
//        return ApiResponse.<AttributeValueDTO>builder()
//                .result(attributeValueService.getAttributeValueById(attributeId)).build();
//    }

    @GetMapping("/value/{attributeTypeId}")
    public ApiResponse<List<AttributeValueDTO>> getAttributeByTypeId(@PathVariable Long attributeTypeId) {
        return ApiResponse.<List<AttributeValueDTO>>builder()
                .result(attributeValueService.findAttributeByTypeId(attributeTypeId)).build();
    }

    @PutMapping("/value/{attributeId}")
    public ApiResponse<AttributeValueDTO> updateAttribute(@PathVariable Long attributeId,
                                                          @RequestBody AttributeValueDTO request) {
        return ApiResponse.<AttributeValueDTO>builder()
                .result(attributeValueService.updateAttributeValue(request.getAttributeType(),
                        attributeId)).build();
    }

    @DeleteMapping("/value/{attributeId}")
    public ApiResponse<Void> deleteAttributeById(@PathVariable Long attributeId) {
        attributeValueService.deleteAttributeValue(attributeId);

        return ApiResponse.<Void>builder()
                .message("Attribute Value Deleted").build();
    }

}
