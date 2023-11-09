(ns project.core
  (:require-macros
   [thi.ng.math.macros :as mm])
  (:require
   [thi.ng.math.core :as m :refer [PI HALF_PI TWO_PI]]
   [thi.ng.color.core :as col]
   [thi.ng.typedarrays.core :as arrays]
   [thi.ng.geom.gl.core :as gl]
   [thi.ng.geom.gl.webgl.constants :as glc]
   [thi.ng.geom.gl.webgl.animator :as anim]
   [thi.ng.geom.gl.buffers :as buf]
   [thi.ng.geom.gl.shaders :as sh]
   [thi.ng.geom.gl.utils :as glu]
   [thi.ng.geom.gl.glmesh :as glm]
   [thi.ng.geom.gl.camera :as cam]
   [thi.ng.geom.core :as g]
   [thi.ng.geom.ptf :as ptf]
   [thi.ng.geom.vector :as v :refer [vec2 vec3]]
   [thi.ng.geom.matrix :as mat :refer [M44]]
   [thi.ng.geom.aabb :as a]
   [thi.ng.geom.attribs :as attr]
   [thi.ng.glsl.core :as glsl :include-macros true]
   [thi.ng.geom.gl.shaders.basic :as basic]
   [thi.ng.geom.circle :as c]
   [thi.ng.geom.polygon :as poly]
   [project.utils :as utils]))

(defn circle
  "Createa a circle returned as a GL buffer spec"
  [r]
  (-> (c/circle r)
      (g/as-polygon 50)
      (gl/as-gl-buffer-spec {:normals false :fixed-color []})))

(defn draw-model
  [model t]
  (let [color (get-in model [:custom :color])
        position (get-in model [:custom :position])
        variation (get-in model [:custom :variation])]
    (-> model
        (update-in [:uniforms] merge
                   {:model (-> M44
                               (g/translate position)
                               (g/rotate-z (-> variation (* 5.2)))
                               (g/rotate-y (-> variation (* t))))
                    ;; should use t to generate a random color sequence somehow 
                    :color color}))))

(defn ^:export main
  []
  (enable-console-print!)
  (let [canvas    (-> "main"
                      utils/by-id
                      utils/fullscreen!)
        gl        (gl/gl-context canvas)
        view-rect (gl/get-viewport-rect gl)
        shader    (sh/make-shader-from-spec gl (basic/make-shader-spec-2d false))
        models     (for [_ (repeat 120 "")] (-> (circle 0.1)
                                               (gl/make-buffers-in-spec gl glc/static-draw)
                                               (assoc-in [:uniforms :proj] (gl/ortho view-rect))
                                               (update-in [:attribs] dissoc :color)
                                               (assoc :shader shader)
                                               ;; position is a concept added by me, not part of geom
                                               (assoc-in [:custom :variation] (utils/float-between 2 5))
                                               (assoc-in [:custom :position] [(utils/float-between -2 2)
                                                                              (utils/float-between -1 1)])

                                               (assoc-in [:custom :color] [(utils/float-between 0.4 1)
                                                                           (utils/float-between 0.2 1)
                                                                           (utils/float-between 0.4 2)
                                                                           (utils/float-between 0.6 2)])))]

    (anim/animate
     (fn [t frame]
       (gl/set-viewport gl view-rect)
       (gl/clear-color-and-depth-buffer gl 1 0.98 0.95 1 1)
       (doseq [model models]
         (gl/draw-with-shader
          gl
          (-> model (draw-model t))))
       true))))

