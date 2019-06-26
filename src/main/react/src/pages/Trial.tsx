import React, {Component} from "react";
import Button from "../components/Button";
import './Trial.css';
import {Scale, Scales} from "./Scale";

export class Trial extends Component<{}, {items: Scales}> {
    componentDidMount() {
        fetch(`http://localhost:9000/scales`)
            .then(result => result.json())
            .then(scales => {
                    return this.setState({items: scales})
                }
            )
    }

    render() {
        return (this.state ? this.buttons(this.state.items[0]) : <span/>)
    }

    private buttons(scale: Scale) {
        return <div className={"trial"}>{scale.description}<br/>
                <ul>{scale.intensities.map((intensity, index) =>
                    <li key={index}><Button intensity={intensity.number} label={intensity.label}/></li>
                )}</ul>
        </div>;
    }
}