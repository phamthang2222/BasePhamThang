# Android Development Agents - Figma to XML UI Rules & Skill Set (`AGENT_FIGMA_UI.md`)

Tài liệu này định nghĩa bộ quy tắc và quy trình chuẩn để AI Agent chuyển đổi thiết kế từ **Figma** sang giao diện **Android XML (ViewBinding)** cho dự án **BasePhamThang**, đảm bảo độ chính xác 100% về icon, hình ảnh, mã màu, font chữ và bố cục.

---

## 1. Quy trình 5 Bước Tự động hóa Figma -> Android XML

Khi người dùng cung cấp link Figma (ví dụ: `https://www.figma.com/design/<fileKey>/<fileName>?node-id=<nodeId>`):

### Bước 1: Đọc Dữ liệu Gốc từ Figma MCP (`figma_figma`)
1. **Bắt buộc** đọc skill `figma-design-to-code` trước khi gọi tool Figma.
2. Trích xuất `fileKey` và `nodeId` (chuyển dấu `-` trong URL `node-id=12-34` thành `12:34`).
3. Gọi MCP tool `get_design_context` với:
   - `fileKey`: khóa file Figma.
   - `nodeId`: ID của Frame/Component cần dựng.
   - `skillNames`: `"figma-design-to-code"`.
   - `clientLanguages`: `"kotlin,xml"`.
   - `clientFrameworks`: `"android"`.
4. Nếu màn hình phức tạp và kết quả trả về dạng rút gọn (*sparse*), gọi `get_design_context` theo batch cho các cụm node con để lấy đầy đủ thông số chi tiết từng thành phần.
5. Nếu chưa có ảnh chụp trực quan của node, gọi thêm `get_screenshot` để làm chuẩn đối chiếu thị giác (Visual Target).

---

### Bước 2: Xuất & Chuẩn hóa Icon và Hình ảnh (`Assets Pipeline`)
**TUYỆT ĐỐI KHÔNG** tự đoán hình dáng icon, không tự gõ bừa `pathData` và không giữ URL tạm của Figma trong code.

1. **Lấy danh sách tài nguyên gốc**:
   - Sử dụng các URL asset trả về từ `get_design_context` hoặc gọi trực tiếp tool `download_assets` (`fileKey`, `nodeId`) để lấy:
     - `svgAssets`: Danh sách các layer vector (icon, logo, hình minh họa вектор).
     - `rawImages` / `export`: Ảnh bitmap gốc (`.png`, `.webp`, `.jpg`).
2. **Xử lý Icon Vector (`svgAssets` -> `res/drawable/ic_*.xml`)**:
   - Tải nội dung file `.svg` từ URL trả về.
   - Chuyển đổi chính xác cấu trúc SVG (`<path>`, `<g>`, `<defs>`, `<linearGradient>`, `<clipPath>`) sang định dạng **Android Vector Drawable XML**:
     - Giữ nguyên kích thước gốc (`android:width`, `android:height`, `android:viewportWidth`, `android:viewportHeight`).
     - Giữ nguyên `android:pathData`, `android:fillColor`, `android:fillAlpha`, `android:strokeColor`, `android:strokeWidth`, `android:strokeLineCap`, `android:strokeLineJoin`, `android:fillType`.
     - Với icon có tô màu Gradient trong SVG, sử dụng `<aapt:attr name="android:fillColor"><gradient ... /></aapt:attr>`.
   - Lưu vào `app/src/main/res/drawable/ic_<ten_mo_ta>.xml` (tên viết thường, ngăn cách bằng dấu `_`).
3. **Xử lý Hình ảnh Bitmap (`rawImages` / `export` -> `res/drawable/img_*` hoặc `bg_*`)**:
   - Tải trực tiếp file ảnh về `app/src/main/res/drawable/`.
   - Đặt tên theo quy ước: `img_<ten_anh>.webp` (hoặc `.png`) cho ảnh minh họa/banner, `bg_<ten_nen>.webp` (hoặc `.png`) cho ảnh nền phức tạp.

---

### Bước 3: Đồng bộ Màu sắc, Shape Drawable, Font chữ & Strings

1. **Mã màu & Độ trong suốt (`app/src/main/res/values/colors.xml`)**:
   - Lấy đúng mã HEX từ Figma. Nếu có độ mờ (`opacity < 100%`), quy đổi sang hệ 8 ký tự `#AARRGGBB` (ví dụ:- `100%` -> `FF`, `80%` -> `CC`, `60%` -> `99`, `50%` -> `80`, `40%` -> `66`, `20%` -> `33`, `10%` -> `1A`).
   - Kiểm tra trong `colors.xml` trước khi thêm mới để tránh trùng lặp:
     - Nếu chưa có, thêm vào `colors.xml` theo chuẩn `<color name="color_<HEX>">#<HEX></color>` hoặc tên ngữ nghĩa của dự án (như `bg_activity`, `blue_in_app`, `neutral_500`).
2. **Background Bo góc, Viền & Gradient (`app/src/main/res/drawable/bg_*.xml`)**:
   - Không dùng ảnh PNG cho các khung hình chữ nhật bo góc, nút bấm hoặc viền đơn giản.
   - Tạo file `<shape>` hoặc `<ripple>` trong `res/drawable/bg_<ten_view>.xml`:
     - `cornerRadius` từ Figma -> `<corners android:radius="...dp" />`.
     - `stroke` từ Figma -> `<stroke android:width="...dp" android:color="@color/..." />`.
     - `gradient` từ Figma -> `<gradient android:angle="..." android:startColor="..." android:endColor="..." android:type="linear" />`.
3. **Font chữ (`app/src/main/res/font/`)**:
   - Ánh xạ chính xác `fontFamily` + `fontWeight` từ Figma sang các file font đã có sẵn trong dự án:
     | Font trên Figma | Weight 400 (Regular) | Weight 500 (Medium) | Weight 600 (SemiBold) | Weight 700 (Bold) | Weight 900 (Black) |
     | :--- | :--- | :--- | :--- | :--- | :--- |
     | **Poppins** | `@font/poppins_regular` | `@font/poppins_medium` | `@font/poppins_semi_bold` | `@font/poppins_bold` | - |
     | **Inter** | `@font/inter_24pt_regular` | `@font/inter_24pt_medium` | `@font/inter_24pt_semibold` | `@font/inter_24pt_bold` | - |
     | **Lato** | `@font/lato_regular` | `@font/lato_medium` | `@font/lato_semibold` | `@font/lato_bold` | `@font/lato_black` |
   - Luôn thêm `android:includeFontPadding="false"` vào `TextView` khi cần căn chỉnh khoảng cách dòng sát với thiết kế Figma.
4. **Chuỗi Văn bản (`app/src/main/res/values/strings.xml`)**:
   - Tách toàn bộ nội dung chữ tĩnh trên Figma vào `res/values/strings.xml` và gọi qua `@string/<ten_key>` (tuân thủ quy tắc không hardcode string trong `AGENTS.md`).

---

### Bước 4: Dựng Layout XML theo Chuẩn `BasePhamThang`

1. **Cấu trúc Layout**:
   - Chuyển đổi `Auto Layout` của Figma sang `ConstraintLayout`, `LinearLayout` (với `orientation` và `layout_weight`), `FrameLayout`, hoặc `RecyclerView` (kèm file `item_*.xml` riêng nếu là danh sách).
   - Không dùng vị trí tuyệt đối cứng nhắc; đảm bảo giao diện thích ứng tốt trên nhiều tỷ lệ màn hình.
2. **Đơn vị Kích thước**:
   - Sử dụng `dp`/`sp` chuẩn hoặc `@dimen/_<n>sdp` / `@dimen/_<n>ssp` (thư viện `sdp-android` & `ssp-android` đã tích hợp sẵn) đồng bộ với các màn hình trong project.
3. **Tận dụng Custom View có sẵn trong `com.phamthang.base.custome_view`**:
   - **`GradientStrokeTextView`**: Dùng khi Figma có chữ viền (`strokeColor`, `strokeWidth`) hoặc chữ hiệu ứng đặc biệt.
   - **`EllipsizeTextView`**: Dùng cho tiêu đề cần tự động cắt `...` gọn gàng.
   - **`MovableText`**: Dùng cho chữ chạy ngang (marquee).
   - **`BottomViewNav`**: Dùng cho các tab điều hướng dưới đáy màn hình.
   - **`LockNestedScrollView`**: Dùng khi cần cuộn nội dung có điều khiển khóa cuộn.
4. **Quy ước ID bắt buộc để đồng bộ với `BaseActivity` và `AGENT_ADS.md`**:
   - **Nút Back**: Đặt `android:id="@+id/icBack"` (hoặc `iv_back`) để `BaseActivity` tự động nhận diện và xử lý sự kiện quay lại.
   - **Khung chứa Quảng cáo Native / Banner**: Đặt `android:id="@+id/frAds"` hoặc `android:id="@+id/bannerContainerView"` ở vị trí đáy/đỉnh theo đúng thiết kế và kịch bản trong `AGENT_ADS.md`.
   - **Khung chứa Collapse Banner**: Đặt `android:id="@+id/collapsible_banner_container_view"`.

---

### Bước 5: Checklist Kiểm tra Chất lượng Giao diện (Verification)

Trước khi hoàn tất task dựng UI từ Figma, AI Agent phải tự kiểm tra:
- [ ] Đã gọi `get_design_context` với skill `figma-design-to-code` chưa?
- [ ] Tất cả các icon và hình ảnh trên màn hình đã được tải từ Figma về `res/drawable/` chưa? (Không thiếu file nào, không tự vẽ bừa pathData).
- [ ] Mã màu nền, màu chữ, màu nút bấm, viền và độ trong suốt (alpha) trong `colors.xml` / `bg_*.xml` đã khớp 100% với thông số trên Figma chưa?
- [ ] Font chữ (`@font/...`), cỡ chữ, kiểu căn lề (`gravity`) và khoảng cách (`padding` / `margin`) đã đúng với node Figma chưa?
- [ ] Các chuỗi văn bản đã được đưa vào `res/values/strings.xml` chưa?
- [ ] File XML đã sẵn sàng cho `ViewBinding` trong `BaseActivity` / `BaseFragment` / `BaseDialogFragment` mà không gặp lỗi biên dịch nào chưa?
