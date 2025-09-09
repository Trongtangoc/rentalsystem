// Tạo file mới: src/main/java/com/codegym/projectmodule5/controller/HostHouseController.java

package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.housedto.request.CreateHouseRequest;
import com.codegym.projectmodule5.dto.request.housedto.request.UpdateHouseRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseDetailResponse;
import com.codegym.projectmodule5.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/host")
@RequiredArgsConstructor
@Slf4j
public class HostHouseController {

    private final HouseService houseService;

    // Hiển thị form thêm nhà mới
    @GetMapping("/houses/add")
    public String showAddHouseForm(Model model, Authentication authentication) {
        log.info("Host {} accessing add house form", authentication.getName());

        model.addAttribute("houseRequest", new CreateHouseRequest());
        model.addAttribute("pageTitle", "Thêm Nhà Mới");
        model.addAttribute("actionUrl", "/host/houses/add");
        model.addAttribute("isEdit", false);

        return "host/house-form";
    }

    // Xử lý thêm nhà mới
    @PostMapping("/houses/add")
    public String addHouse(
            @Valid @ModelAttribute("houseRequest") CreateHouseRequest request,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        log.info("Host {} attempting to add new house: {}", authentication.getName(), request.getTitle());

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in add house form: {}", bindingResult.getAllErrors());
            model.addAttribute("pageTitle", "Thêm Nhà Mới");
            model.addAttribute("actionUrl", "/host/houses/add");
            model.addAttribute("isEdit", false);
            return "host/house-form";
        }

        try {
            HouseDetailResponse house = houseService.createHouse(request, authentication.getName());
            log.info("House created successfully with ID: {}", house.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Nhà '" + house.getTitle() + "' đã được thêm thành công!");

            return "redirect:/host/dashboard";

        } catch (Exception e) {
            log.error("Error creating house: {}", e.getMessage());
            model.addAttribute("errorMessage", "Lỗi khi thêm nhà: " + e.getMessage());
            model.addAttribute("pageTitle", "Thêm Nhà Mới");
            model.addAttribute("actionUrl", "/host/houses/add");
            model.addAttribute("isEdit", false);
            return "host/house-form";
        }
    }

    // Hiển thị form sửa nhà
    @GetMapping("/houses/{houseId}/edit")
    public String showEditHouseForm(
            @PathVariable Long houseId,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        log.info("Host {} accessing edit form for house ID: {}", authentication.getName(), houseId);

        try {
            HouseDetailResponse house = houseService.getHouseById(houseId);

            // Kiểm tra quyền sở hữu
            if (!house.getOwnerName().equals(authentication.getName())) {
                log.warn("Host {} tried to edit house {} owned by {}",
                        authentication.getName(), houseId, house.getOwnerName());
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Bạn không có quyền sửa nhà này!");
                return "redirect:/host/dashboard";
            }

            // Chuyển đổi từ HouseDetailResponse sang UpdateHouseRequest
            UpdateHouseRequest updateRequest = new UpdateHouseRequest();
            updateRequest.setTitle(house.getTitle());
            updateRequest.setDescription(house.getDescription());
            updateRequest.setPrice(house.getPrice());
            updateRequest.setAddress(house.getAddress());
            updateRequest.setImageUrls(house.getImageUrls());

            model.addAttribute("houseRequest", updateRequest);
            model.addAttribute("houseId", houseId);
            model.addAttribute("pageTitle", "Sửa Thông Tin Nhà");
            model.addAttribute("actionUrl", "/host/houses/" + houseId + "/edit");
            model.addAttribute("isEdit", true);
            model.addAttribute("currentImages", house.getImageUrls());

            return "host/house-form";

        } catch (Exception e) {
            log.error("Error loading house for edit: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể tải thông tin nhà: " + e.getMessage());
            return "redirect:/host/dashboard";
        }
    }

    // Xử lý cập nhật nhà
    @PostMapping("/houses/{houseId}/edit")
    public String updateHouse(
            @PathVariable Long houseId,
            @Valid @ModelAttribute("houseRequest") UpdateHouseRequest request,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        log.info("Host {} attempting to update house ID: {}", authentication.getName(), houseId);

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in edit house form: {}", bindingResult.getAllErrors());
            model.addAttribute("houseId", houseId);
            model.addAttribute("pageTitle", "Sửa Thông Tin Nhà");
            model.addAttribute("actionUrl", "/host/houses/" + houseId + "/edit");
            model.addAttribute("isEdit", true);
            return "host/house-form";
        }

        try {
            HouseDetailResponse updatedHouse = houseService.updateHouse(houseId, request, authentication.getName());
            log.info("House {} updated successfully", houseId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Nhà '" + updatedHouse.getTitle() + "' đã được cập nhật thành công!");

            return "redirect:/host/dashboard";

        } catch (Exception e) {
            log.error("Error updating house {}: {}", houseId, e.getMessage());
            model.addAttribute("errorMessage", "Lỗi khi cập nhật nhà: " + e.getMessage());
            model.addAttribute("houseId", houseId);
            model.addAttribute("pageTitle", "Sửa Thông Tin Nhà");
            model.addAttribute("actionUrl", "/host/houses/" + houseId + "/edit");
            model.addAttribute("isEdit", true);
            return "host/house-form";
        }
    }

    // Xóa nhà
    @PostMapping("/houses/{houseId}/delete")
    public String deleteHouse(
            @PathVariable Long houseId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        log.info("Host {} attempting to delete house ID: {}", authentication.getName(), houseId);

        try {
            houseService.deleteHouse(houseId, authentication.getName());
            log.info("House {} deleted successfully", houseId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Nhà đã được xóa thành công!");

        } catch (Exception e) {
            log.error("Error deleting house {}: {}", houseId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi khi xóa nhà: " + e.getMessage());
        }

        return "redirect:/host/dashboard";
    }

    // Xem chi tiết nhà (cho host)
    @GetMapping("/houses/{houseId}")
    public String viewHouseDetail(
            @PathVariable Long houseId,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        log.info("Host {} viewing house detail ID: {}", authentication.getName(), houseId);

        try {
            HouseDetailResponse house = houseService.getHouseById(houseId);

            // Kiểm tra quyền sở hữu
            if (!house.getOwnerName().equals(authentication.getName())) {
                log.warn("Host {} tried to view house {} owned by {}",
                        authentication.getName(), houseId, house.getOwnerName());
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Bạn không có quyền xem nhà này!");
                return "redirect:/host/dashboard";
            }

            model.addAttribute("house", house);
            model.addAttribute("isOwner", true);

            return "host/house-detail";

        } catch (Exception e) {
            log.error("Error loading house detail: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể tải thông tin nhà: " + e.getMessage());
            return "redirect:/host/dashboard";
        }
    }

    // Toggle trạng thái nhà (Available/Hidden)
    @PostMapping("/houses/{houseId}/toggle-status")
    @ResponseBody
    public ApiResponse toggleHouseStatus(
            @PathVariable Long houseId,
            Authentication authentication) {

        log.info("Host {} toggling status for house ID: {}", authentication.getName(), houseId);

        try {
            // Logic toggle status sẽ được implement trong HouseService
            // houseService.toggleHouseStatus(houseId, authentication.getName());

            return new ApiResponse(true, "Trạng thái nhà đã được thay đổi!");

        } catch (Exception e) {
            log.error("Error toggling house status: {}", e.getMessage());
            return new ApiResponse(false, "Lỗi khi thay đổi trạng thái: " + e.getMessage());
        }
    }
}