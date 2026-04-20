# Design System Specification: Zen Kinetic

## 1. Overview & Creative North Star
The Creative North Star for this design system is **"The Mindful Pulse."** 

In the competitive landscape of the Chinese digital market, we move away from the cluttered "super-app" aesthetic toward a "Zen Kinetic" philosophy. This system balances the tranquility of expansive white space with the energetic "pulse" of a sophisticated coral-red primary. It is an editorial-first approach that treats the screen not as a container for data, but as a curated gallery. We break the "template" look through intentional asymmetry, generous breathing room, and a rejection of traditional structural lines in favor of tonal depth.

## 2. Colors
Our palette is rooted in a "Humanistic Tech" vibe—warmth meets precision.

### The Palette
*   **Primary (#b51a1a):** A vibrant, sophisticated coral-red. It represents energy and vitality.
*   **Secondary (#9e4038):** A muted terracotta used for supporting elements and semantic depth.
*   **Tertiary (#006762):** A deep teal to provide a "Zen" contrast to the heat of the primary.
*   **Neutral Surfaces:** Ranging from `surface-container-lowest` (#ffffff) to `surface-dim` (#d9dadc), these form the foundation of our "soft grey" depth.

### The "No-Line" Rule
To maintain a premium, high-end feel, **1px solid borders are strictly prohibited for sectioning.** Boundaries must be defined solely through background color shifts. For example, a card using `surface-container-lowest` should sit atop a `surface-container-low` background. This creates a soft, natural transition that feels architectural rather than "boxed in."

### Surface Hierarchy & Nesting
Treat the UI as a series of stacked physical layers. Use the `surface-container` tiers to define importance:
*   **Surface:** The base canvas.
*   **Surface-Container-Low:** Use for large background sections to create depth.
*   **Surface-Container-Lowest:** Reserved for the most prominent "cards" or "content blocks" to make them pop against the lower tiers.

### The "Glass & Gradient" Rule
For floating elements (modals, bottom sheets, or navigation bars), use **Glassmorphism**. Combine semi-transparent surface colors with a `20px` to `40px` backdrop blur. For primary CTAs, use a subtle linear gradient from `primary` (#b51a1a) to `primary_container` (#d93630) at a 135-degree angle to add "visual soul" and dimension.

## 3. Typography
The typography is designed to be authoritative yet approachable, optimized for high-density CJK (Chinese, Japanese, Korean) environments while maintaining a Western editorial flair.

*   **Display & Headlines (Plus Jakarta Sans / PingFang SC Medium):** Large, bold, and confident. Use `display-lg` (3.5rem) for hero moments to create an "Editorial" feel. Ensure a letter-spacing of `-0.02em` for headlines to keep the energy tight.
*   **Body (Manrope / PingFang SC Regular):** Focus on readability. We use a **generous line height (1.6 to 1.8)** to prevent the "wall of text" effect common in complex apps.
*   **Hierarchy as Identity:** Use high contrast in scale. A `display-sm` headline paired with a `body-md` description creates a professional, trustworthy rhythm that guides the eye through the "Zen" space.

## 4. Elevation & Depth
We convey hierarchy through **Tonal Layering** rather than traditional drop shadows.

*   **The Layering Principle:** Place `surface-container-lowest` cards on a `surface-container-low` section. This creates a "soft lift" that feels premium and integrated.
*   **Ambient Shadows:** When an element must float (e.g., a floating action button), use extra-diffused shadows. 
    *   *Shadow Specs:* Blur: 32px, Spread: -4px, Opacity: 6% of the `on-surface` color.
    *   Shadows should never be pure black; they should be a deep, tinted version of the surface color to mimic natural ambient light.
*   **The "Ghost Border" Fallback:** If a container requires more definition for accessibility, use the `outline-variant` token at **15% opacity**. This "Ghost Border" provides a hint of structure without breaking the minimalist aesthetic.

## 5. Components

### Buttons
*   **Primary:** High-pill or `xl` (3rem) rounded corners. Uses the signature gradient. Text is `on-primary`.
*   **Secondary:** `surface-container-high` background with `primary` colored text. No border.
*   **Tertiary:** Transparent background with `primary` text and a slight `surface-bright` hover state.

### Cards
*   **The Standard:** Use `xl` (3rem) or `lg` (2rem) corner radius. 
*   **Rule:** No dividers inside cards. Use vertical white space (from the Spacing Scale) or subtle background shifts (e.g., a `surface-variant` header within a card) to separate content.

### Inputs
*   **Minimalist Field:** Use `surface-container-highest` as the fill. 
*   **State:** On focus, the field transitions to a `primary` "Ghost Border" (20% opacity) rather than a heavy solid line.

### Progress & Kinetic Elements
*   **Zen Rings:** For fitness or data tracking, use the `primary` color for active states and `surface-container-highest` for the empty track. Use rounded caps on all strokes to maintain the "Soft" vibe.

### Chips
*   **Filter Chips:** `md` (1.5rem) roundedness. Unselected: `surface-container-high`. Selected: `primary` with `on-primary` text.

## 6. Do's and Don'ts

### Do
*   **Do** embrace asymmetry. Center a headline but left-align the body text to create a modern, editorial look.
*   **Do** use "Breathing Space." If you think there is enough margin, add 8px more.
*   **Do** use `surface-container-lowest` for the main content area to make it feel like "clean paper."

### Don't
*   **Don't** use 1px solid black or grey borders. This instantly kills the "High-End" feel.
*   **Don't** use standard "Material Design" blue for links. Stick to the `primary` coral-red or `tertiary` teal.
*   **Don't** crowd the interface. If a screen feels busy, move secondary actions into a "More" glassmorphic bottom sheet.
*   **Don't** use harsh, high-contrast shadows. If the shadow is clearly visible at a glance, it is too heavy.