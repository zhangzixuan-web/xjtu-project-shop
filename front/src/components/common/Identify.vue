<!--
  @file Identify.vue
  @description This component generates a graphical verification code (captcha) image on an HTML5 canvas.
  It's used to prevent automated submissions by requiring users to input the distorted text from the image.
  The appearance of the captcha is highly customizable through props.
-->
<template>
  <div class="s-canvas">
    <canvas id="s-canvas" :width="contentWidth" :height="contentHeight"></canvas>
  </div>
</template>

<script>
export default {
  name: 'Identify',
  props: {
    // The verification code string to be rendered.
    identifyCode: {
      type: String,
      default: '1234'
    },
    // Minimum font size for the characters.
    fontSizeMin: {
      type: Number,
      default: 28
    },
    // Maximum font size for the characters.
    fontSizeMax: {
      type: Number,
      default: 40
    },
    // Minimum value for the RGB color components of the background.
    backgroundColorMin: {
      type: Number,
      default: 180
    },
    // Maximum value for the RGB color components of the background.
    backgroundColorMax: {
      type: Number,
      default: 240
    },
    // Minimum value for the RGB color components of the text.
    colorMin: {
      type: Number,
      default: 50
    },
    // Maximum value for the RGB color components of the text.
    colorMax: {
      type: Number,
      default: 160
    },
    // Minimum value for the RGB color components of the interference lines.
    lineColorMin: {
      type: Number,
      default: 40
    },
    // Maximum value for the RGB color components of the interference lines.
    lineColorMax: {
      type: Number,
      default: 180
    },
    // Minimum value for the RGB color components of the interference dots.
    dotColorMin: {
      type: Number,
      default: 0
    },
    // Maximum value for the RGB color components of the interference dots.
    dotColorMax: {
      type: Number,
      default: 255
    },
    // The width of the canvas.
    contentWidth: {
      type: Number,
      default: 112
    },
    // The height of the canvas.
    contentHeight: {
      type: Number,
      default: 40
    }
  },
  methods: {
    /**
     * Generates a random integer within a specified range.
     * @param {number} min - The minimum value (inclusive).
     * @param {number} max - The maximum value (exclusive).
     * @returns {number} A random integer.
     */
    randomNum (min, max) {
      return Math.floor(Math.random() * (max - min) + min)
    },

    /**
     * Generates a random RGB color string.
     * @param {number} min - The minimum value for each RGB component.
     * @param {number} max - The maximum value for each RGB component.
     * @returns {string} A CSS rgb color string, e.g., "rgb(120,80,200)".
     */
    randomColor (min, max) {
      var r = this.randomNum(min, max)
      var g = this.randomNum(min, max)
      var b = this.randomNum(min, max)
      return 'rgb(' + r + ',' + g + ',' + b + ')'
    },

    /**
     * Main drawing function that renders the entire captcha image.
     * It orchestrates the drawing of the background, text, and interference patterns.
     */
    drawPic () {
      var canvas = document.getElementById('s-canvas')
      var ctx = canvas.getContext('2d')
      ctx.textBaseline = 'bottom'

      // 1. Draw the background with a random color.
      ctx.fillStyle = this.randomColor(
          this.backgroundColorMin,
          this.backgroundColorMax
      )
      ctx.fillRect(0, 0, this.contentWidth, this.contentHeight)

      // 2. Draw each character of the verification code.
      for (let i = 0; i < this.identifyCode.length; i++) {
        this.drawText(ctx, this.identifyCode[i], i)
      }

      // 3. Draw interference lines.
      this.drawLine(ctx)

      // 4. Draw interference dots (noise).
      this.drawDot(ctx)
    },

    /**
     * Draws a single character onto the canvas with random transformations.
     * @param {CanvasRenderingContext2D} ctx - The canvas rendering context.
     * @param {string} txt - The character to draw.
     * @param {number} i - The index of the character in the code string.
     */
    drawText (ctx, txt, i) {
      ctx.fillStyle = this.randomColor(this.colorMin, this.colorMax)
      ctx.font =
          this.randomNum(this.fontSizeMin, this.fontSizeMax) + 'px SimHei' // Random font size.
      var x = (i + 1) * (this.contentWidth / (this.identifyCode.length + 1))
      var y = this.randomNum(this.fontSizeMax, this.contentHeight - 5)
      var deg = this.randomNum(-30, 30) // Random rotation angle.

      // Apply transformations: translate to position and rotate.
      ctx.translate(x, y)
      ctx.rotate(deg * Math.PI / 270)
      ctx.fillText(txt, 0, 0)

      // Restore the canvas context to its original state.
      ctx.rotate(-deg * Math.PI / 270)
      ctx.translate(-x, -y)
    },

    /**
     * Draws interference lines on the canvas to make the captcha harder to read by bots.
     * @param {CanvasRenderingContext2D} ctx - The canvas rendering context.
     */
    drawLine (ctx) {
      // Draw 2 random lines.
      for (let i = 0; i < 2; i++) {
        ctx.strokeStyle = this.randomColor(
            this.lineColorMin,
            this.lineColorMax
        )
        ctx.beginPath()
        ctx.moveTo(
            this.randomNum(0, this.contentWidth),
            this.randomNum(0, this.contentHeight)
        )
        ctx.lineTo(
            this.randomNum(0, this.contentWidth),
            this.randomNum(0, this.contentHeight)
        )
        ctx.stroke()
      }
    },

    /**
     * Draws interference dots (noise) on the canvas.
     * @param {CanvasRenderingContext2D} ctx - The canvas rendering context.
     */
    drawDot (ctx) {
      // Draw 20 random dots.
      for (let i = 0; i < 20; i++) {
        ctx.fillStyle = this.randomColor(0, 255)
        ctx.beginPath()
        ctx.arc(
            this.randomNum(0, this.contentWidth),
            this.randomNum(0, this.contentHeight),
            1, // dot radius
            0,
            2 * Math.PI
        )
        ctx.fill()
      }
    }
  },
  watch: {
    // Watch for changes in the identifyCode prop and redraw the canvas accordingly.
    identifyCode () {
      this.drawPic()
    }
  },
  // When the component is mounted, draw the initial captcha image.
  mounted () {
    this.drawPic()
  }
}
</script>

<style scoped>
.s-canvas {
  height: 38px;
}
.s-canvas canvas{
  margin-top: 1px;
  margin-left: 8px;
}
</style>
