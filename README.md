ImageDensity
============
<p>
<div><b>Author:</b> Riyaz (riyaz@riyazm.com)</div>
<h2>Environment Details</h2>
<div><b>Language:</b> JAVA</div>
<div><b>JDK version:</b> 1.6.0</div>
<div><b>IDE:</b> ECLIPSE</div>

<h2>About</h2>

<p>This is the most simplest method to find the density of a image, which helps where you need to compare two images based on its density.</p>
<p>
<b>The soul algorith of this app is simple as follows</b>
  <ol>
      <li>Get the image</li>
      <li>Convert the image into Grayscale</li>
      <li>Then perform edge detection using ConvolveOp()</li>
      <li>Invert the image using LookupOp()</li>
      <li>Finally grab the pixels using PixelGrabber() and add all the pixels. That is it! You have obtained the density of the image !</li>      
  </ol>
</p>
</p>
