//package uzumtech.notification.controller;
//
////import uzumtech.notification.service.MerchantService;
////import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
///**
// * REST контроллер для управления мерчантами
// * Handles: регистрация, логин, получение информации, обновление
// */
//@RestController
//@RequestMapping("/api/merchants")
//@CrossOrigin(origins = "*", maxAge = 3600)
//public class MerchantController {
//
//    private final MerchantService merchantService;
//
//    public MerchantController(MerchantService merchantService) {
//        this.merchantService = merchantService;
//    }
//
//    /**
//     * Регистрация нового мерчанта
//     * POST /api/merchants/registration
//     *
//     * @param request - JSON с данными мерчанта для регистрации
//     * @return JSON с информацией о созданном мерчанте (статус 201)
//     */
//    @PostMapping("/registration")
//    public ResponseEntity<?> register(@Valid @RequestBody Object request) {
//        return ResponseEntity
//            .status(HttpStatus.CREATED)
//            .body(merchantService.registerMerchant(request));
//    }
//
//    /**
//     * Логин мерчанта и получение JWT токена
//     * POST /api/merchants/login
//     *
//     * @param request - JSON с login и password
//     * @return JSON с JWT токеном и информацией о мерчанте (статус 200)
//     */
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody Object request) {
//        return ResponseEntity
//            .ok(merchantService.login(request));
//    }
//
//    /**
//     * Получить информацию о мерчанте по ID
//     * GET /api/merchants/{merchantId}
//     *
//     * @param merchantId - ID мерчанта
//     * @return JSON с информацией о мерчанте (статус 200)
//     */
//    @GetMapping("/{merchantId}")
//    public ResponseEntity<?> getMerchantById(@PathVariable Long merchantId) {
//        return ResponseEntity
//            .ok(merchantService.getMerchantById(merchantId));
//    }
//
//    /**
//     * Получить информацию о текущем авторизованном мерчанте
//     * GET /api/merchants/profile/me
//     * Требует авторизацию (JWT токен)
//     *
//     * @return JSON с информацией о текущем мерчанте (статус 200)
//     */
//    @GetMapping("/profile/me")
//    @PreAuthorize("hasRole('MERCHANT')")
//    public ResponseEntity<?> getCurrentMerchant() {
//        return ResponseEntity
//            .ok(merchantService.getCurrentMerchant());
//    }
//
//    /**
//     * Обновить информацию о мерчанте
//     * PUT /api/merchants/{merchantId}
//     * Требует авторизацию (JWT токен)
//     *
//     * @param merchantId - ID мерчанта
//     * @param request - JSON с новыми данными мерчанта
//     * @return JSON с обновленной информацией о мерчанте (статус 200)
//     */
//    @PutMapping("/{merchantId}")
//    @PreAuthorize("hasRole('MERCHANT')")
//    public ResponseEntity<?> updateMerchant(
//        @PathVariable Long merchantId,
//        @Valid @RequestBody Object request) {
//        return ResponseEntity
//            .ok(merchantService.updateMerchant(merchantId, request));
//    }
//
//    /**
//     * Удалить мерчанта
//     * DELETE /api/merchants/{merchantId}
//     * Требует авторизацию (JWT токен)
//     *
//     * @param merchantId - ID мерчанта для удаления
//     * @return JSON с сообщением об успехе (статус 200)
//     */
//    @DeleteMapping("/{merchantId}")
//    @PreAuthorize("hasRole('MERCHANT')")
//    public ResponseEntity<?> deleteMerchant(@PathVariable Long merchantId) {
//        merchantService.deleteMerchant(merchantId);
//        return ResponseEntity.ok().build();
//    }
//}