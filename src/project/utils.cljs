(ns project.utils)

(defn by-id
  [id]
  (js/document.getElementById id))

(defn fullscreen!
  "sets the given canvas to fullscreen and returns it."
  [canvas]
  (let [width js/window.innerWidth 
        height js/window.innerHeight]
    (set! canvas.width width)
    (set! canvas.height height))
  canvas)

(defn print!
  [value]
  (println value)
  value)

(defn float-between [a b]
  (print! (+ a (* (rand) (- b a)))))

