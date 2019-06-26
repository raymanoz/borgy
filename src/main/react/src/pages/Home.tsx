import React, {Component} from "react";
import {Scale, Scales} from "./Scale";

export class Home extends Component<{}, { scales: Scales, trialName: String, trialScale: String }> {
    constructor(props: Readonly<{}>) {
        super(props);
        this.state = {
            scales: [],
            trialName: "",
            trialScale: ""
        };
        this.renderScaleRadio = this.renderScaleRadio.bind(this);
    }

    componentDidMount() {
        fetch(`http://localhost:9000/scales`)
            .then(result => result.json())
            .then(s => this.setState({scales: s}))
    }

    render() {
        return <span>
            <h1>Borgy</h1>
            <span id={"newTrial"}>
                <fieldset>
                    <legend>Start a new trial</legend>
                    <label htmlFor="trial-name">Name</label><input id="trial-name" type="text" onChange={event => this.onTrialNameChanged(event.target.value)}/><br/>
                    <label>Scale</label><br/>
                    {this.state ? this.state.scales.map(this.renderScaleRadio) : <span>Loading scales</span>}
                    <button>Begin</button>
                </fieldset>
            </span>
        </span>
    }

    private onTrialNameChanged(value: String) {
        this.setState({trialName: value})
    }

    private onTrialScaleChanged(value: String) {
        this.setState({trialScale: value})
    }

    private renderScaleRadio(scale: Scale) {
        let scaleName = `${scale.name}`;
        let radioId = `scale-${scale.name}`;
        return <span key={scaleName}>
            <input id={radioId} type="radio" name="scale" value={scaleName} onChange={event => this.onTrialScaleChanged(event.target.value)}/>
            <label htmlFor={radioId}>{scaleName}</label><br/>
        </span>;
    }
}